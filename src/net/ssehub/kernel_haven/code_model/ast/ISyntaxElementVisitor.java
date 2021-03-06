package net.ssehub.kernel_haven.code_model.ast;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A visitor for traversing an {@link ISyntaxElement}-based AST. The default operation will visit all nested elements,
 * but won't do any other operation.
 * 
 * @author El-Sharkawy
 */
public interface ISyntaxElementVisitor {
    
    // C-Preprocessor
    
    /**
     * <b>CPP:</b> Visits a C-preprocessor block. This may be one of the following blocks:
     * <ul>
     *     <li>&#35;if</li>
     *     <li>&#35;ifdef</li>
     *     <li>&#35;ifndef</li>
     *     <li>&#35;elif</li>
     *     <li>&#35;else</li>
     * </ul>
     * Provides default Visitation: <b>true</b>
     * @param block The block to visit.
     */
    public default void visitCppBlock(@NonNull CppBlock block) {
        for (int i = 0; i < block.getNestedElementCount(); i++) {
            block.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>CPP:</b> Visits a C-preprocessor statement. This may be one of the following blocks:
     * <ul>
     *     <li>&#35;include</li>
     *     <li>&#35;define</li>
     *     <li>&#35;undef</li>
     *     <li>&#35;warning</li>
     *     <li>&#35;error</li>
     *     <li>&#35;line</li>
     *     <li>&#35;empty</li>
     *     <li>&#35;pragma</li>
     * </ul>
     * Provides default Visitation: <b>true</b>
     * @param cppStatement The statement to visit.
     */
    public default void visitCppStatement(@NonNull CppStatement cppStatement) {
        ICode expression = cppStatement.getExpression();
        if (expression != null) {
            expression.accept(this);
        }
    }
    
    // C-Code
    
    /**
     * <b>C-Code:</b> Visits a C code file. This is usually the entry point for visitation as it is the top level
     * element of all C code files.<br/>
     * Provides default Visitation: <b>true</b>
     * @param file The file to visit.
     */
    public default void visitFile(@NonNull File file) {
        for (int i = 0; i < file.getNestedElementCount(); i++) {
            file.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>C-Code:</b> Visits a function. These are usually at the top level of a file.
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param function The function to visit.
     */
    public default void visitFunction(@NonNull Function function) {
        function.getHeader().accept(this);
        for (int i = 0; i < function.getNestedElementCount(); i++) {
            function.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>C-Code:</b> Visits a single statement. This represents assignments, declarations, etc. (basically everything
     * that ends with a semicolon and does something).
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param statement The statement to visit.
     */
    public default void visitSingleStatement(@NonNull SingleStatement statement) {
        statement.getCode().accept(this);
    }
    
    /**
     * <b>C-Code:</b> Visits a compound statement. This is a block of statements
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param block The block to visit.
     */
    public default void visitCompoundStatement(@NonNull CompoundStatement block) {
        for (int i = 0; i < block.getNestedElementCount(); i++) {
            block.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>C-Code:</b> Visits a list of unparsed code objects.
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param code The code to visit.
     */
    public default void visitCodeList(@NonNull CodeList code) {
        for (int i = 0; i < code.getNestedElementCount(); i++) {
            code.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>C-Code/CPP:</b> Visits unparsed code. <br/>
     * Provides default Visitation: <b>false</b>
     * @param code The unparsed code element to visit.
     */
    public default void visitCode(@NonNull Code code) {
        // nothing to do
    }
    
    /**
     * <b>C-Code:</b> Visits a type definition.
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param typeDef The type definition to visit.
     */
    public default void visitTypeDefinition(@NonNull TypeDefinition typeDef) {
        typeDef.getDeclaration().accept(this);
        for (int i = 0; i < typeDef.getNestedElementCount(); i++) {
            typeDef.getNestedElement(i).accept(this);
        }
    }

    /**
     * <b>C-Code:</b> Visits a label.
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param label The label to visit.
     */
    public default void visitLabel(@NonNull Label label) {
        label.getCode().accept(this);
    }
    
    /**
     * <b>C-Code:</b> Visits a comment.
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param comment The Comment to visit.
     */
    public default void visitComment(@NonNull Comment comment) {
        comment.getComment().accept(this);
    }
    
    // C Control structures
    
    /**
     * <b>C control structure:</b> Visits a branching statement (if, else if or else).
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param branchStatement The branching statement to visit.
     */
    public default void visitBranchStatement(@NonNull BranchStatement branchStatement) {
        ICode condition = branchStatement.getIfCondition();
        if (null != condition) {
            condition.accept(this);
        }
        
        for (int i = 0; i < branchStatement.getNestedElementCount(); i++) {
            branchStatement.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>C control structure:</b> Visits a switch statement.
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param switchStatement The switch to visit.
     */
    public default void visitSwitchStatement(@NonNull SwitchStatement switchStatement) {
        switchStatement.getHeader().accept(this);
        
        for (int i = 0; i < switchStatement.getNestedElementCount(); i++) {
            switchStatement.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>C control structure:</b> Visits a case statement.
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param caseStatement The case statement to visit.
     */
    public default void visitCaseStatement(@NonNull CaseStatement caseStatement) {
        ICode condition = caseStatement.getCaseCondition();
        if (null != condition) {
            condition.accept(this);
        }
        
        for (int i = 0; i < caseStatement.getNestedElementCount(); i++) {
            caseStatement.getNestedElement(i).accept(this);
        }
    }
    
    /**
     * <b>C control structure:</b> Visits a loop statement (while, do-while or for).
     * <br/>
     * Provides default Visitation: <b>true</b>
     * 
     * @param loop The loop to visit.
     */
    public default void visitLoopStatement(@NonNull LoopStatement loop) {
        loop.getLoopCondition().accept(this);
        
        for (int i = 0; i < loop.getNestedElementCount(); i++) {
            loop.getNestedElement(i).accept(this);
        }
    }
    
}
