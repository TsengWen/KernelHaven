package net.ssehub.kernel_haven.util.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.kernel_haven.util.io.csv.AllCsvTests;

/**
 * Tests for util.io package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    AllCsvTests.class,
    
    TableRowMetadataTest.class,
    TableCollectionReaderFactoryTest.class,
    TableCollectionReaderFactoryTest.class,
    })
public class AllIoTests {

}
