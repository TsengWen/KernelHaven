package net.ssehub.kernel_haven.util.io;

import java.io.IOException;
import java.util.Set;

/**
 * A collection of tables. Access to the different nested tables is available via {@link ITableReader}s and
 * {@link ITableWriter}s. Each table has a uniquely identifying name.
 *
 * @author Adam
 */
public interface ITableCollection {
    
    /**
     * Creates a reader for the table with the specified name.
     * 
     * @param name The name of the table to create a reader for.
     * @return A reader for the specified table. <code>null</code> if no table with the specified name exists. 
     * 
     * @throws IOException If creating the reader fails.
     */
    public ITableReader getReader(String name) throws IOException;
    
    /**
     * Retrieves a set of all available table names.
     * 
     * @return A set containing all tables that are in this collection. Never <code>null</code>, but may be empty.
     * 
     * @throws IOException If creating the set fails.
     */
    public Set<String> getTableNames() throws IOException;
    
    /**
     * Creates a writer for the table with the specified name. If such a table already exists, it is deleted and
     * overwritten by this writer.
     * 
     * @param name The name of the table to create.
     * @return A writer for the table with the specified name.
     * 
     * @throws IOException If creating the writer fails.
     */
    public ITableWriter getWriter(String name) throws IOException;

}