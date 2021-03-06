package net.ssehub.kernel_haven.util.io;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.maybeNull;
import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;
import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNullArrayWithNotNullContent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Metadata about the fields for a class that is annotated via the {@link TableRow} annotation.
 *
 * @author Adam
 */
public class TableRowMetadata {
    
    private @NonNull Class<?> rowClass;
    
    private @NonNull Method @NonNull [] fields;
    
    private @NonNull String @NonNull [] headers;
    
    /**
     * Creates the metadata for the given table row class.
     * 
     * @param tableRowClass The class to create this metadata for.
     * 
     * @throws IllegalArgumentException If the given class does not have the {@link TableRow} annotation.
     */
    public TableRowMetadata(@NonNull Class<?> tableRowClass) throws IllegalArgumentException {
        if (!isTableRow(tableRowClass)) {
            throw new IllegalArgumentException("Can only create TableMetadata for classes annotated with @TableRow");
        }
        
        this.rowClass = tableRowClass;
        
        Map<Integer, Method> methods = new TreeMap<>();
        for (Method method: rowClass.getMethods()) {
            TableElement annotation = maybeNull(method.getAnnotation(TableElement.class));
            if (annotation != null) {
                methods.put(annotation.index(), method);
            }
        }

        Method[] fields = new Method[methods.size()];
        String[] headers = new String[methods.size()];
        int index = 0;
        for (Map.Entry<Integer, Method> entry : methods.entrySet()) {
            // we silently ignore if index != entry.getKey(); the order is correct, because of the TreeMap; we don't
            //  care about "holes" in the ordering
            fields[index] = entry.getValue();
            headers[index] = notNull(entry.getValue().getAnnotation(TableElement.class)).name();
            index++;
        }
        
        this.fields = notNullArrayWithNotNullContent(fields);
        this.headers = notNullArrayWithNotNullContent(headers); 
    }
    
    /**
     * Retrieves the header names for this table.
     * 
     * @return The header names. Not <code>null</code>.
     */
    public @NonNull Object @NonNull [] getHeaders() {
        return headers;
    }
    
    /**
     * Retrieves the field content for the given instance of the class that this metadata represents.
     * 
     * @param instance The instance that the field content should be retrieved from.
     * @return The field contents, in the correct ordering.
     * 
     * @throws ReflectiveOperationException If retrieving the field contents fails (e.g. because the given object is
     *      not an instance of the class that this metadata is made for).
     */
    public @NonNull Object @NonNull [] getContent(@NonNull Object instance) throws ReflectiveOperationException {
        Object[] values = new Object[fields.length];
        
        try {
            int index = 0;
            for (Method field : fields) {
                Object result = field.invoke(instance);
                values[index++] = result;
            }
            
        } catch (IllegalArgumentException e) {
            throw new ReflectiveOperationException("Can't access field value", e);
        }
        
        return notNullArrayWithNotNullContent(values);
    }
    
    /**
     * Checks whether the given object is an instance of the class that this metadata is made for.
     * 
     * @param instance The instance to check.
     * @return Whether the instance is an instance of this class.
     */
    public boolean isSameClass(@NonNull Object instance) {
        return instance.getClass() == rowClass;
    }
    
    /**
     * Tests whether the given class is annotated with the {@link TableRow} annotation.
     * 
     * @param clazz The class to check.
     * @return Whether the given class has the annotation.
     */
    public static boolean isTableRow(@NonNull Class<?> clazz) {
        TableRow annotation = maybeNull(clazz.getAnnotation(TableRow.class));
        return annotation != null;
    }

}
