import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BinaryExprMutator implements MutationProcessor<MethodDeclaration> {
    private static void flipComparator(BinaryExpr binaryExpr) {
        binaryExpr.setOperator(Objects.requireNonNull(BinaryExpOps.getFlippedComparator(binaryExpr.getOperator())));
    }

    @Override
    public List<MutatedMethod> process(MethodDeclaration method) {
        List<MutatedMethod> mutations = new ArrayList<>();

        method.accept(new Visitor(mutations, method), null);

        return mutations;
    }

    private static class Visitor extends VoidVisitorAdapter<Void> {
        private final        List<MutatedMethod> mutations;
        private final        MethodDeclaration   method;
        private              boolean             foundString     = false;
        private static final List<String>        probablyStrings = Arrays.asList(
                "name", "string", "str", "doc", "comment", "desc", "title", "regex", "exp", "msg", "message");

        Visitor(List<MutatedMethod> mutations, MethodDeclaration method) {
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

            if (!BinaryExpOps.isFlippableComparator(op) ||
                    (foundString && BinaryExpOps.isPossibleStringOp(op))) {
                return;
            }

            foundString = prevFoundString;// pop the old value

            final var left = n.getLeft();
            final var right = n.getRight();

            n.setLeft(right);
            n.setRight(left);
            flipComparator(n);

            mutations.add(MutatedMethod.from(method));

            n.setLeft(left);
            n.setRight(right);
            n.setOperator(op);
        }
    }
}
