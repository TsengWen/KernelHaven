package net.ssehub.kernel_haven;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.kernel_haven.analysis.AbstractAnalysis;
import net.ssehub.kernel_haven.build_model.AbstractBuildModelExtractor;
import net.ssehub.kernel_haven.build_model.BuildModel;
import net.ssehub.kernel_haven.code_model.AbstractCodeModelExtractor;
import net.ssehub.kernel_haven.code_model.SourceFile;
import net.ssehub.kernel_haven.config.BuildExtractorConfiguration;
import net.ssehub.kernel_haven.config.CodeExtractorConfiguration;
import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.VariabilityExtractorConfiguration;
import net.ssehub.kernel_haven.util.ExtractorException;
import net.ssehub.kernel_haven.util.Logger;
import net.ssehub.kernel_haven.variability_model.AbstractVariabilityModelExtractor;
import net.ssehub.kernel_haven.variability_model.VariabilityModel;

/**
 * Tests the {@link PipelineConfigurator} class.
 * 
 * @author Adam
 * @author Manu
 * @author Moritz
 * @author Johannes
 */
public class PipelineConfiguratiorTest {

    /**
     * Sets up the logger.
     */
    @BeforeClass
    public static void beforeClass() {
        Logger.init();
    }

    /**
     * Tests whether instantiateExtractor correctly throws an exception if the
     * VM extractor is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testVmExtractorWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", "thisClassDoesNotExist");
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();
    }

    /**
     * Tests whether instantiateExtractor correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testVmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();

        assertThat(configurator.getVmExtractor(), instanceOf(DummyVmExtractor.class));
        
    }
    
    /**
     * A dummy class for testing purposes.
     */
    public static class DummyVmExtractor extends AbstractVariabilityModelExtractor {

        @Override
        protected void init(VariabilityExtractorConfiguration config) throws SetUpException {
        }

        @Override
        protected VariabilityModel runOnFile(File target) throws ExtractorException {
            return null;
        }

        @Override
        protected String getName() {
            return null;
        }


    }


    /**
     * Tests whether instantiateExtractor correctly instantiates a class after loading jars
     * from a directory containing a packaged version of this class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testPluginVmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", "net.ssehub.kernel_haven.DummyPluginVmExtractor");
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("plugins_dir", "testdata/plugins");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.loadPlugins();
        configurator.instantiateExtractors();

        assertThat(configurator.getVmExtractor(), notNullValue());

        List<Method> methods = Arrays.asList(configurator.getVmExtractor().getClass().getDeclaredMethods());

        List<String> methodNames = new ArrayList<>();
        for (Method method : methods) {
            methodNames.add(method.getName());
        }

        assertThat(methodNames, hasItem(equalTo("runOnFile")));
        assertThat(methodNames, hasItem(equalTo("init")));
        assertThat(methodNames, hasItem(equalTo("getName")));
    }

    /**
     * Tests whether the vmextractor is null if a it is represented by a
     * classname that was not loaded via the plugins loader.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testPluginVmExtractorNegative() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", "net.ssehub.kernel_haven.NotExistingExtractor");
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("plugins_dir", "testdata/plugins");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.loadPlugins();

        assertThat(configurator.getVmExtractor(), nullValue());
    }

    /**
     * Tests whether instantiateAnalysis correctly throws an exception if the
     * analysis is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testAnalysisWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", "thisClassDoesNotExist");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateAnalysis();
    }

    /**
     * Tests whether instantiateAnalysis correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testAnalysisInit() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", DummyAnalysis.class.getName());
        config.setProperty("output_dir", "not_used");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateAnalysis();

        assertThat(configurator.getAnalysis(), instanceOf(DummyAnalysis.class));
    }

    /**
     * Tests whether instantiateAnalysis correctly throws an
     * {@link SetUpException} if the constructor throws one.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testAnalysisSetUpException() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", DummyAnalysis.class.getName());
        config.setProperty("fail", "true");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateAnalysis();

        assertThat(configurator.getAnalysis(), instanceOf(DummyAnalysis.class));
    }
    
    /**
     * Tests if a class that does not implement IAnalysis correctly throws an exception.
     * 
     * @throws SetUpException wanted.
     */
    @Test(expected = SetUpException.class)
    public void testNoIAnalysis() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("analysis.class", WrongDummyAnalysis.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateAnalysis();
    }

    /**
     * A dummy analysis for testing purposes.
     */
    private static class DummyAnalysis extends AbstractAnalysis {

        /**
         * Creates a new dummy analysis.
         * 
         * @param config
         *            The configuration file.
         * 
         * @throws SetUpException
         *             If properties contains a key called "fail".
         */
        public DummyAnalysis(Configuration config) throws SetUpException {
            super(config);
            
            if (config.getProperty("fail") != null) {
                throw new SetUpException();
            }
        }

        @Override
        public void run() {

        }

    }
    
    /**
     * A dummy analysis that does not implement IAnalyis, so that a ClassCastException is provoked.
     */
    private static class WrongDummyAnalysis {
        
        /**
         * ctor.
         * @param config The user config.
         */
        @SuppressWarnings("unused")
        public WrongDummyAnalysis(Configuration config) {
        }
        
    }
    
    /**
     * Tests whether instantiateExtractor correctly throws an exception if the
     * BM extractor is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testBmExtractorWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", "thisClassDoesNotExist");
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();
    }

    /**
     * Tests whether instantiateExtractor correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testBmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();

        assertThat(configurator.getBmExtractor(), instanceOf(DummyBmExtractor.class));
    }
    
    /**
     * A dummy class for testing purposes.
     */
    public static class DummyBmExtractor extends AbstractBuildModelExtractor {

        @Override
        protected void init(BuildExtractorConfiguration config) throws SetUpException {
        }

        @Override
        protected BuildModel runOnFile(File target) throws ExtractorException {
            return null;
        }

        @Override
        protected String getName() {
            return null;
        }


    }
    
    /**
     * Tests whether instantiateExtractor correctly throws an exception if the
     * CM extractor is set to a non existing class.
     * 
     * @throws SetUpException
     *             wanted.
     */
    @Test(expected = SetUpException.class)
    public void testCmExtractorWrongClass() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", "thisClassDoesNotExist");
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();
    }

    /**
     * Tests whether instantiateExtractor correctly instantiates a class.
     * 
     * @throws SetUpException
     *             unwanted.
     */
    @Test
    public void testCmExtractor() throws SetUpException {
        Properties config = new Properties();
        config.setProperty("variability.extractor.class", DummyVmExtractor.class.getName());
        config.setProperty("build.extractor.class", DummyBmExtractor.class.getName());
        config.setProperty("code.extractor.class", DummyCmExtractor.class.getName());
        PipelineConfigurator configurator = new PipelineConfigurator();
        configurator.init(new TestConfiguration(config));
        configurator.instantiateExtractors();

        assertThat(configurator.getCmExtractor(), instanceOf(DummyCmExtractor.class));
    }
    
    /**
     * A dummy class for testing purposes.
     */
    public static class DummyCmExtractor extends AbstractCodeModelExtractor {

        @Override
        protected void init(CodeExtractorConfiguration config) throws SetUpException {
        }

        @Override
        protected SourceFile runOnFile(File target) throws ExtractorException {
            return null;
        }

        @Override
        protected String getName() {
            return "DummyCmExtrator";
        }


    }


}