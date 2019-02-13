import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class LiteralMutator implements MutationProcessor<MethodDeclaration> {
    @Override
    public List<MutatedMethod> process(MethodDeclaration method) {
        List<MutatedMethod> mutations = new ArrayList<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IntegerLiteralExpr n, Void arg) {
                var old = n.asInt();// TODO: there is a bug here I think. Try parsing big int
                n.setInt(404040404);
//                mutations.add(MutatedMethod.from(method)); // TODO
                n.setInt(old);
                throw new RuntimeException("Not implemented");
            }
        }, null);

        return mutations;
    }
}
