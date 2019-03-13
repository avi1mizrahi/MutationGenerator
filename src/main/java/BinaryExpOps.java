import com.github.javaparser.ast.expr.BinaryExpr;

import java.util.Objects;

class BinaryExpOps {
    static boolean isCommutative(BinaryExpr.Operator binaryOperator) {
        switch (binaryOperator) {
            case OR:
            case AND:
            case BINARY_OR:
            case BINARY_AND:
            case XOR:
            case EQUALS:
            case NOT_EQUALS:
            case PLUS:
            case MULTIPLY:
                return true;
        }
        return false;
    }

    static boolean isComparison(BinaryExpr.Operator binaryOperator) {
        switch (binaryOperator) {
            case EQUALS:
            case NOT_EQUALS:
            case LESS:
            case GREATER:
            case LESS_EQUALS:
            case GREATER_EQUALS:
                return true;
        }
        return false;
    }

    static boolean isFlippableComparator(BinaryExpr.Operator binaryOperator) {
        return getFlippedComparator(binaryOperator) != null;
    }

    static BinaryExpr.Operator getFlippedComparator(BinaryExpr.Operator binaryOperator) {
        if (isCommutative(binaryOperator))
            return binaryOperator;

        switch (binaryOperator) {
            case LESS:
                return BinaryExpr.Operator.GREATER;
            case GREATER:
                return BinaryExpr.Operator.LESS;
            case LESS_EQUALS:
                return BinaryExpr.Operator.GREATER_EQUALS;
            case GREATER_EQUALS:
                return BinaryExpr.Operator.LESS_EQUALS;
        }
        return null;
    }

    static BinaryExpr.Operator getNegatedComparator(BinaryExpr.Operator binaryOperator) {
        switch (binaryOperator) {
            case EQUALS:
                return BinaryExpr.Operator.NOT_EQUALS;
            case NOT_EQUALS:
                return BinaryExpr.Operator.EQUALS;
            case LESS:
                return BinaryExpr.Operator.GREATER_EQUALS;
            case GREATER:
                return BinaryExpr.Operator.LESS_EQUALS;
            case LESS_EQUALS:
                return BinaryExpr.Operator.GREATER;
            case GREATER_EQUALS:
                return BinaryExpr.Operator.LESS;
        }
        return null;
    }

    static boolean isPossibleStringOp(BinaryExpr.Operator op) {
        return op == BinaryExpr.Operator.PLUS;
    }
}
