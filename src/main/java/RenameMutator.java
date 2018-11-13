import spoon.refactoring.CtRenameLocalVariableRefactoring;
import spoon.reflect.code.CtLocalVariable;

public class RenameMutator extends OneByOneMutator<CtLocalVariable> {
    private final CtRenameLocalVariableRefactoring refactoring = new CtRenameLocalVariableRefactoring();
    private String oldName;

    @Override
    public void doMutation(CtLocalVariable variable) {
        oldName = variable.getSimpleName();
        refactoring.setTarget(variable);
        refactoring.setNewName("bla");
        refactoring.refactor();
    }

    @Override
    public void undoMutation(CtLocalVariable variable) {
        refactoring.setTarget(variable);
        refactoring.setNewName(oldName);
        refactoring.refactor();
    }

    @Override
    public Class getElementType() {
        return CtLocalVariable.class;
    }


}
