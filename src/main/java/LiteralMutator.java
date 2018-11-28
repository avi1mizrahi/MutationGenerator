import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class LiteralMutator implements MethodMutationProcessor {
    @Override
    public List<String> process(MethodDeclaration method) {
        List<String> mutations = new ArrayList<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IntegerLiteralExpr n, Void arg) {
                var old = n.asInt();// TODO: there is a bug here I think. Try parsing big int
                n.setInt(404040404);
                mutations.add(method.toString());
                n.setInt(old);
            }
        }, null);

        return mutations;
    }
}
