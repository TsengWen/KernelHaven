package net.ssehub.kernel_haven.variability_model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.Util;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Tests the variability model cache.
 *
 * @author Johannes
 * @author Kevin
 * @author Moritz
 * @author Adam
 * @author Marvin
 */
@SuppressWarnings("null")
public class VariabilityModelCacheTest {

    private File cacheDir;

    /**
     * Registers the {@link TristateVariableSerializer}.
     */
    @BeforeClass
    public static void registerSerializer() {
        VariabilityVariableSerializerFactory.INSTANCE.registerSerializer(TristateVariable.class.getName(),
                new TristateVariableSerializer());
    }
    
    /**
     * Creates the cache directory for each test.
     */
    @Before
    public void setUp() {
        cacheDir = new File("testdata/tmp_cache");
        cacheDir.mkdir();
        assertThat(cacheDir.isDirectory(), is(true));
    }

    /**
     * Deletes the cache directory after each test.
     * 
     * @throws IOException
     *             unwanted.
     */
    @After
    public void tearDown() throws IOException {
        Util.deleteFolder(cacheDir);
    }

    /**
     * A simple tristate for testing.
     */
    private static class TristateVariable extends VariabilityVariable {

        private int moduleNumber;

        /**
         * Creates a new variable.
         * 
         * @param name
         *            The name of the new variable. Must not be null.
         * @param dimacsNumber
         *            The number that this variable has in the DIMACS
         *            representation of the variability model.
         * @param moduleNumber
         *            The number that the module part of this variable has in
         *            the DIMACS representation of the variability model.
         */
        public TristateVariable(String name, int dimacsNumber, int moduleNumber) {
            super(name, "tristate", dimacsNumber);
            this.moduleNumber = moduleNumber;
        }

        /**
         * Creates a tristate variable from a given VariabilityVariable.
         * 
         * @param var
         *            the variability variable.
         * @param moduleNumber
         *            the tristate module number.
         */
        private TristateVariable(VariabilityVariable var, int moduleNumber) {
            this(var.getName(), var.getDimacsNumber(), moduleNumber);

            if (var.getSourceLocations() != null) {
                for (SourceLocation location : var.getSourceLocations()) {
                    this.addLocation(location);
                }
            }
        }
        
        @Override
        public void getDimacsMapping(Map<Integer, String> mapping) {
            mapping.put(getDimacsNumber(), getName());
            mapping.put(moduleNumber, getName() + "_MODULE");
        }

        @Override
        public String toString() {
            return "TristateVariable [name=" + getName() + ", type=" + getType() + ", dimacsNumber=" + getDimacsNumber()
                    + ", moduleNumber=" + moduleNumber + ", codeLocations="
                    + (getSourceLocations() == null ? "null" : getSourceLocations().toString()) + "]";
        }

        @Override
        public int hashCode() {
            return super.hashCode() + new Integer(moduleNumber).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            boolean result = super.equals(obj);
            if (result) {
                if (obj instanceof TristateVariable) {
                    TristateVariable other = (TristateVariable) obj;
                    result = other.moduleNumber == this.moduleNumber;

                } else {
                    result = false;
                }
            }
            return result;
        }

    }
    
    /**
     * A serializer for {@link TristateVariable}s.
     */
    private static class TristateVariableSerializer extends VariabilityVariableSerializer {

        @Override
        protected @NonNull List<@NonNull String> serializeImpl(@NonNull VariabilityVariable variable) {
            TristateVariable triVar = (TristateVariable) variable;
            
            List<@NonNull String> result = super.serializeImpl(variable);
            result.add(String.valueOf(triVar.moduleNumber));
            
            return result;
        }
        
        @Override
        protected @NonNull VariabilityVariable deserializeImpl(@NonNull String @NonNull [] csv) throws FormatException {
            VariabilityVariable variable = super.deserializeImpl(csv);
            TristateVariable result = new TristateVariable(variable, Integer.parseInt(csv[DEFAULT_SIZE]));
            return result;
        }
        
        @Override
        protected void checkLength(@NonNull String @NonNull [] csv) throws FormatException {
            if (csv.length != DEFAULT_SIZE + 1) {
                throw new FormatException("Expected " + (DEFAULT_SIZE + 1) + " fields");
            }
        }
        
    }

    /**
     * Test serializing, and deserializing the result.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test
    public void testCaching() throws IOException, FormatException {
        File dimacsFile = new File("testdata/vmCaching/testmodel.dimacs");
        Set<VariabilityVariable> set = new HashSet<VariabilityVariable>();

        TristateVariable alpha = new TristateVariable("ALPHA", 1, 2);
        alpha.addLocation(new SourceLocation(new File("foxtrot/unicorn/charlie/kilo.kconfig"), 12));
        alpha.addLocation(new SourceLocation(new File("fruchtkernhafen.kconfig"), 42));
        set.add(alpha);

        set.add(new TristateVariable("BETA", 5, 4));

        VariabilityVariable gamma = new VariabilityVariable("GAMMA", "bool", 3);
        gamma.addLocation(new SourceLocation(new File("baumstaemmer.kconfig"), 14));
        set.add(gamma);
        VariabilityModel originalVm = new VariabilityModel(dimacsFile, set);

        VariabilityModelCache cache = new VariabilityModelCache(cacheDir);

        // write
        cache.write(originalVm);

        // read
        VariabilityModel readVm = cache.read(new File(""));

        // check if constraint model is equal
        BufferedReader r1 = new BufferedReader(new FileReader(readVm.getConstraintModel()));
        BufferedReader r2 = new BufferedReader(new FileReader(originalVm.getConstraintModel()));

        String newLine;
        String originalLine;
        while ((newLine = r1.readLine()) != null) {
            originalLine = r2.readLine();
            assertThat(originalLine, notNullValue());

            assertThat(newLine, is(originalLine));
        }
        assertThat(r1.readLine(), nullValue());

        r1.close();
        r2.close();

        // check if variables are equal
        assertThat(readVm.getVariables(), is(originalVm.getVariables()));
    }

    /**
     * Tests three cases first: if the code location path and the line number
     * matches the given path in the cache with a normal variable. second: if
     * the code location path, line number matches the given path in the cache
     * with a tristate variable. third : if the code location and line number is
     * null (as it is in the cache).
     * 
     * @throws FormatException
     *             if a wrong format appears.
     * @throws IOException
     *             if a generally io exception appears.
     */
    @Test
    public void testCodeLocationRead() throws FormatException, IOException {

        File cacheLocation = new File("testdata/vmCaching/cache_codelocation/");
        assertThat(cacheLocation.getAbsolutePath() + " does not exist ", cacheLocation.exists(), is(true));
        assertThat(cacheLocation.getAbsolutePath() + " does is no directory", cacheLocation.isDirectory(), is(true));
        VariabilityModelCache cache = new VariabilityModelCache(cacheLocation);
        VariabilityModel varmodel = cache.read(new File(""));
        assertThat(varmodel, notNullValue());
        Map<String, VariabilityVariable> varMap = varmodel.getVariableMap();

        List<SourceLocation> locationsGamma = varMap.get("GAMMA").getSourceLocations();

        assertThat(locationsGamma.size(), is(2));
        assertThat(locationsGamma.get(0).getLineNumber(), is(15));
        assertThat(locationsGamma.get(0).getSource(), equalTo(new File("path/to/code/code.c")));
        assertThat(locationsGamma.get(1).getLineNumber(), is(11));
        assertThat(locationsGamma.get(1).getSource(), equalTo(new File("path/to/code/code2.c")));

        varMap.get("ALPHA").getSourceLocations();
        List<SourceLocation> locationAlpha = varMap.get("ALPHA").getSourceLocations();
        assertThat(locationAlpha.get(0).getSource(), equalTo(new File("path/to/code/code3.c")));
        assertThat(locationAlpha.get(0).getLineNumber(), is(42));
        List<SourceLocation> locationsBeta = varMap.get("BETA").getSourceLocations();
        assertThat(locationsBeta, nullValue());
    }

    /**
     * Tests if invalid class names are correctly thrown as
     * {@link FormatException}.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testInvalidClassName() throws FormatException, IOException {
        VariabilityModelCache cache = new VariabilityModelCache(new File("testdata/vmCaching/cache1"));
        cache.read(new File(""));
    }

    /**
     * Tests if the VariabilityVariable correctly throws an
     * {@link FormatException} with an invalid csv file.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testMalFormCSV() throws FormatException, IOException {
        VariabilityModelCache cache = new VariabilityModelCache(new File("testdata/vmCaching/cache2"));
        cache.read(new File(""));
    }

    /**
     * Tests if the cache correctly returns <code>null</code> on empty cache.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             unwanted.
     */
    @Test()
    public void testEmptyCache() throws FormatException, IOException {
        VariabilityModelCache cache = new VariabilityModelCache(new File("testdata/vmCaching/cache3"));
        VariabilityModel vm = cache.read(new File(""));

        assertThat(vm, nullValue());
    }

    /**
     * Tests if invalid codelocation correctly result in exceptions when read.
     * 
     * @throws IOException
     *             unwanted.
     * @throws FormatException
     *             wanted.
     */
    @Test(expected = FormatException.class)
    public void testInvalidCodeLocation() throws FormatException, IOException {
        VariabilityModelCache cache = new VariabilityModelCache(
                new File("testdata/vmCaching/cache_codelocation_invalid"));
        VariabilityModel vm = cache.read(new File(""));

        assertThat(vm, nullValue());
    }

}
