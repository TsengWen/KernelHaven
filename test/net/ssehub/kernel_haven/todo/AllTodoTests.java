package net.ssehub.kernel_haven.todo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for todo package.
 */
@RunWith(Suite.class)
@SuiteClasses({
    CPPUtilsTest.class,
    NonBooleanPreperationTest.class,
})
public class AllTodoTests {

}