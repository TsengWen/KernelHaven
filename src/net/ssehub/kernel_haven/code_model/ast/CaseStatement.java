package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents a <tt>case</tt> or a <tt>default</tt> block of a switch statement.
 * @author El-Sharkawy
 *
 */
public class CaseStatement extends SyntaxElementWithChildreen {
    
    public static enum CaseType {
        CASE, DEFAULT;
    }

    private @Nullable SyntaxElement caseCondition;
    
    private @NonNull CaseType type;
    
    public CaseStatement(@NonNull Formula presenceCondition, @Nullable SyntaxElement caseCondition,
            @NonNull CaseType type) {
        
        super(presenceCondition);
        this.caseCondition = caseCondition;
        this.type = type;
    }
    
    public @Nullable SyntaxElement getCaseCondition() {
        return caseCondition;
    }

    @Override
    protected @NonNull String elementToString(@NonNull String indentation) {
        SyntaxElement caseCondition = this.caseCondition;
        
        String result = type.name() + "\n";
        if (caseCondition != null) {
            result += caseCondition.toString(indentation + "\t");
        }
        
        return result;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCaseStatement(this);
    }

}