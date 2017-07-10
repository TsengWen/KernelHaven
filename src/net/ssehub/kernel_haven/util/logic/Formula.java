package net.ssehub.kernel_haven.util.logic;

import java.io.Serializable;

/**
 * A boolean formula.
 * 
 * @author Adam (from KernelMiner project)
 * @author Sascha El-Sharkawy
 */
public abstract class Formula implements Serializable {
    
    private static final long serialVersionUID = -2811872324947850301L;

    /**
     * Evaluates this formula, based on the values set for the {@link Variable} in this formula.
     * 
     * @return Whether this formula evaluates to <code>true</code> or <code>false</code>.
     */
    public abstract boolean evaluate();
    
    /**
     * Converts the formula into a string representation.
     * 
     * @return A string representation of this formula, in a C-style like format.
     */
    @Override
    public abstract String toString();
    
    /**
     * Checks whether two {@link Formula}s are equal. {@link Formula}s are equal,
     * if they contain the same operators in the same hierarchy with the same
     * variable names.
     */
    @Override
    public abstract boolean equals(Object obj);
    
    
    @Override
    public abstract int hashCode();
    
    /**
     * Returns the number of literals (the number of involved variables).
     * If a variable is multiple times involved, it will also counted multiple times.
     * @return The number of used variables (&ge; 0).
     */
    public abstract int getLiteralSize();
    
    /**
     * Visiting method for visitors.
     * @param visitor A visitor, which shall visit <tt>this</tt> formula.
     */
    public abstract void accept(IFormulaVisitor visitor);
}