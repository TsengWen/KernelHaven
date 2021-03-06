package net.ssehub.kernel_haven.util.io;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.io.IOException;

import net.ssehub.kernel_haven.util.AbstractHandlerRegistry;
import net.ssehub.kernel_haven.util.io.csv.CsvFileSet;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * <p>
 * Factory for creating {@link ITableCollection}s for single files based on filename suffix. The resulting
 * {@link ITableCollection} should only be used for reading. 
 * </p>
 * <p>
 * Handlers can register themselves via the {@link #registerHandler(String, Class)} method. All handler classes must
 * have a constructor that takes a single {@link File} argument.
 * </p>
 * <p>
 * By default, {@link CsvFileSet} is registered as the handler for .csv files.
 * </p>
 *  
 * @author Adam
 */
public class TableCollectionReaderFactory extends AbstractHandlerRegistry<String, ITableCollection> {

    /**
     * The singleton instance for this factory.
     */
    public static final @NonNull TableCollectionReaderFactory INSTANCE = new TableCollectionReaderFactory();
    
    /**
     * Only used for singleton instance.
     */
    private TableCollectionReaderFactory() {
        registerHandler("csv", CsvFileSet.class);
    }
    
    /**
     * Opens the given file with a matching handler. The handler is identified via the file suffix.
     * 
     * @param file The file to open.
     * @return An {@link ITableCollection} for the given file.
     * 
     * @throws IOException If no handler is available for the given file, or instantiating the handler fails.
     */
    public @NonNull ITableCollection openFile(@NonNull File file) throws IOException {
        String suffix = getSuffix(file);
        Class<? extends ITableCollection> handlerClass = getHandler(suffix);
        
        if (handlerClass == null) {
            throw new IOException("No handler for suffix " + suffix);
        }
        
        try {
            return  notNull(handlerClass.getConstructor(File.class).newInstance(file));
            
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IOException("Can't instantiate handler class", e);
        }
    }
    
    /**
     * Returns the suffix of the given file.
     * 
     * @param file The file to get the suffix for.
     * @return The suffix of the file.
     * 
     * @throws IOException If the file has no suffix.
     */
    public static @NonNull String getSuffix(@NonNull File file) throws IOException {
        int dotIndex = file.getName().lastIndexOf('.');
        
        if (dotIndex == -1 || dotIndex == file.getName().length() - 1) {
            throw new IOException("Filename \"" + file.getName() + "\" has no suffix");
        }
        
        String suffix = notNull(file.getName().substring(dotIndex + 1));
        
        return suffix;
    }
    
}
