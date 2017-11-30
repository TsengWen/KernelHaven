package net.ssehub.kernel_haven.util.logic;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link DisjunctionQueue}.
 * @author El-Sharkawy
 *
 */
public class DisjunctionQueueTests {

    /**
     * Tests the general ability to create disjunctions, without any simplifications.
     */
    @Test
    public void  testCreateDisjunction() {
        DisjunctionQueue queue = new DisjunctionQueue(false);
        
        // 1st test with constant values
        queue.add(True.INSTANCE);
        queue.add(False.INSTANCE);
        Formula f = queue.getDisjunction(null);
        Assert.assertEquals(new Disjunction(True.INSTANCE, False.INSTANCE), f);
        
        // 2nd test with variable
        Variable varA = new Variable("A");
        queue.add(varA);
        queue.add(varA);
        f = queue.getDisjunction(null);
        Assert.assertEquals(new Disjunction(varA, varA), f);
    }
    
    /**
     * Tests if can simplify constantly true parts.
     */
    @Test
    public void  testSimplifyTrue() {
        DisjunctionQueue queue = new DisjunctionQueue(true);
        
        // 1st test: Add true first
        queue.add(True.INSTANCE);
        queue.add(False.INSTANCE);
        Formula f = queue.getDisjunction(null);
        Assert.assertEquals(True.INSTANCE, f);
        
        // 2nd test: Add true last
        queue.add(False.INSTANCE);
        queue.add(True.INSTANCE);
        f = queue.getDisjunction(null);
        Assert.assertEquals(True.INSTANCE, f);
    }
    
    /**
     * Test that it avoids insertion of the same element twice.
     */
    @Test
    public void  testAvoidDoubledElements() {
        DisjunctionQueue queue = new DisjunctionQueue(true);
        
        Variable varA = new Variable("A");
        queue.add(varA);
        queue.add(varA);
        Formula f = queue.getDisjunction(null);
        Assert.assertEquals(varA, f);
    }
}
