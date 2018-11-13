import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;

public class CommutativeExprMutator extends OneByOneMutator<CtBinaryOperator<?>> {
    private CtExpression lhs;
    private CtExpression rhs;

    private static boolean isCommutative(CtBinaryOperator binaryOperator) {
        switch (binaryOperator.getKind()) {
            case OR:
            case AND:
            case BITOR:
            case BITXOR:
            case BITAND:
            case EQ:
            case NE:
            case PLUS:
            case MUL:
                return true;
        }
        return false;
    }

    @Override
    public void doMutation(CtBinaryOperator<?> op) {
        lhs = op.getLeftHandOperand();
        rhs = op.getRightHandOperand();
        op.setRightHandOperand(lhs);
        op.setLeftHandOperand(rhs);
    }

    @Override
    public void undoMutation(CtBinaryOperator<?> op) {
        op.setRightHandOperand(rhs);
        op.setLeftHandOperand(lhs);
    }

    @Override
    public Class getElementType() {
        return CtBinaryOperator.class;
    }

    @Override
    public boolean isToBeProcessed(CtBinaryOperator candidate) {
        return super.isToBeProcessed(candidate) && isCommutative(candidate);
    }

}
