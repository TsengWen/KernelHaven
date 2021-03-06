package net.ssehub.kernel_haven.util.io;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Marks an (getter of an) attribute inside a class marked via the {@link TableRow} as a single column element. The
 * {@link #toString()} method of the return value will be used to fill the field in the table. The marked getter must
 * return a value that a {@link #toString()} can be called on, and must not have any parameters. If it returns null,
 * then an empty string ("") is inserted into the table at that position. The method marked by this annotation must be
 * public, otherwise it is ignored.
 *
 * @author Adam
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface TableElement {
    
    /**
     * Defines the position of this column in the table. The first column has the index 0.
     * 
     * @return The index of the column that this field should be placed in. Must not be negative. All fields in a given
     *      {@link TableRow} class must form a coherent interval, starting from 0.
     */
    int index();
    
    /**
     * The name of this field.
     * 
     * @return The name of this field, to be used in the table header.
     */
    @NonNull String name() default "";

}
