import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class IfElseMutator implements MutationProcessor<MethodDeclaration> {
    @Override
    public List<MutatedMethod> process(MethodDeclaration method) {
        List<MutatedMethod> mutations = new ArrayList<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt n, Void arg) {
                n.getCondition().ifBinaryExpr(binaryExpr -> {
                    final BinaryExpr.Operator op = binaryExpr.getOperator();
                    if ((op == BinaryExpr.Operator.NOT_EQUALS || op == BinaryExpr.Operator.EQUALS)
                            && n.getElseStmt().isPresent()) {
                        Statement thenStmt = n.getThenStmt().clone();
                        Statement elseStmt = n.getElseStmt().get().clone();
                        n.setThenStmt(elseStmt);
                        n.setElseStmt(thenStmt);
                        binaryExpr.setOperator(op == BinaryExpr.Operator.EQUALS ?
                                                       BinaryExpr.Operator.NOT_EQUALS :
                                                       BinaryExpr.Operator.EQUALS);

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
