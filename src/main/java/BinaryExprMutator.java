import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class BinaryExprMutator implements MethodMutationProcessor {

    private static boolean isCommutative(BinaryExpr.Operator binaryOperator) {
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

    private static boolean isFlippableComparator(BinaryExpr.Operator binaryOperator) {
        return getFlippedComparator(binaryOperator) != null;
    }

    private static void flipComparator(BinaryExpr binaryExpr) {
        binaryExpr.setOperator(getFlippedComparator(binaryExpr.getOperator()));
    }

    private static BinaryExpr.Operator getFlippedComparator(BinaryExpr.Operator binaryOperator) {
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

    @Override
    public List<String> process(MethodDeclaration method) {
        List<String> mutations = new ArrayList<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(BinaryExpr n, Void arg) {
                super.visit(n, arg);

                final var op = n.getOperator();

                if (!isFlippableComparator(op)) {
                    return;
                }

                final var left = n.getLeft();
                final var right = n.getRight();

                n.setLeft(right);
                n.setRight(left);
                flipComparator(n);

                mutations.add(method.toString());

                n.setLeft(left);
                n.setRight(right);
                n.setOperator(op);
            }
        }, null);

        return mutations;
    }
}
