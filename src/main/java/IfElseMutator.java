import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IfElseMutator implements MutationProcessor<MethodDeclaration> {
    private static void invertComparator(BinaryExpr binaryExpr) {
        binaryExpr.setOperator(Objects.requireNonNull(BinaryExpOps.getNegatedComparator(binaryExpr.getOperator())));
    }

    @Override
    public List<MutatedMethod> process(MethodDeclaration method) {
        List<MutatedMethod> mutations = new ArrayList<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt n, Void arg) {
                n.getCondition().ifBinaryExpr(binaryExpr -> {
                    final BinaryExpr.Operator op = binaryExpr.getOperator();
                    if (BinaryExpOps.isComparison(op) && n.getElseStmt().isPresent()) {
                        Statement thenStmt = n.getThenStmt().clone();
                        Statement elseStmt = n.getElseStmt().get().clone();
                        n.setThenStmt(elseStmt);
                        n.setElseStmt(thenStmt);

                        invertComparator(binaryExpr);

                        mutations.add(MutatedMethod.from(method));

                        n.setThenStmt(thenStmt);
                        n.setElseStmt(elseStmt);
                        binaryExpr.setOperator(op);
                    }
                });

                super.visit(n, arg);
            }
        }, null);

        return mutations;
    }
}
