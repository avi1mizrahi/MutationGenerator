import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        binaryExpr.setOperator(Objects.requireNonNull(getFlippedComparator(binaryExpr.getOperator())));
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

    private static boolean isPossibleStringOp(BinaryExpr.Operator op) {
        return op == BinaryExpr.Operator.PLUS;
    }

    @Override
    public List<String> process(MethodDeclaration method) {
        List<String> mutations = new ArrayList<>();

        method.accept(new Visitor(mutations, method), null);

        return mutations;
    }

    private static class Visitor extends VoidVisitorAdapter<Void> {
        private final List<String> mutations;
        private final MethodDeclaration method;
        private boolean foundString = false;
        private static final List<String> probablyStrings = Arrays.asList(
                "name", "string", "str", "doc", "comment", "desc", "title", "regex", "exp", "msg", "message");

        Visitor(List<String> mutations, MethodDeclaration method) {
            this.mutations = mutations;
            this.method = method;
        }

        @Override
        public void visit(StringLiteralExpr n, Void arg) {
            foundString = true;
        }

        @Override
        public void visit(SimpleName n, Void arg) {
            if (!foundString && probablyStrings.stream().anyMatch(n.getIdentifier().toLowerCase()::contains))
                foundString = true;
            super.visit(n, arg);
        }

        @Override
        public void visit(BinaryExpr n, Void arg) {
            var prevFoundString = foundString;// maintain a "stack"
            foundString = false;

            super.visit(n, arg);

            final var op = n.getOperator();

            if (!isFlippableComparator(op) || (foundString && isPossibleStringOp(op))) {
                return;
            }

            foundString = prevFoundString;// pop the old value

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
    }
}
