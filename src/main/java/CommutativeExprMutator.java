import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;

public class CommutativeExprMutator extends OneByOneMutator<CtBinaryOperator<?>> {
    private CtExpression lhs;
    private CtExpression rhs;
    private BinaryOperatorKind operatorKind;

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

    private static boolean isFlippableComparator(CtBinaryOperator binaryOperator) {
        return getFlippedComparator(binaryOperator.getKind()) != null;
    }

    private static void flipComparator(CtBinaryOperator binaryOperator) {
        binaryOperator.setKind(getFlippedComparator(binaryOperator.getKind()));
    }

    private static BinaryOperatorKind getFlippedComparator(BinaryOperatorKind binaryOperatorKind) {
        switch (binaryOperatorKind) {
            case LT:
                return BinaryOperatorKind.GT;
            case GT:
                return BinaryOperatorKind.LT;
            case LE:
                return BinaryOperatorKind.GE;
            case GE:
                return BinaryOperatorKind.LE;
        }
        return null;
    }

    @Override
    public void doMutation(CtBinaryOperator<?> op) {
        lhs = op.getLeftHandOperand();
        rhs = op.getRightHandOperand();
        operatorKind = op.getKind();
        op.setRightHandOperand(lhs);
        op.setLeftHandOperand(rhs);
        if (isFlippableComparator(op))
            flipComparator(op);
    }

    @Override
    public void undoMutation(CtBinaryOperator<?> op) {
        op.setRightHandOperand(rhs);
        op.setLeftHandOperand(lhs);
        op.setKind(operatorKind);
    }

    @Override
    public Class getElementType() {
        return CtBinaryOperator.class;
    }

    @Override
    public boolean isToBeProcessed(CtBinaryOperator candidate) {
        return super.isToBeProcessed(candidate) && (isCommutative(candidate) || isFlippableComparator(candidate));
    }

}
