import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class WhileToForMutator implements MutationProcessor<MethodDeclaration> {
    @Override
    public List<MutatedMethod> process(MethodDeclaration method) {
        List<MutatedMethod> mutations = new ArrayList<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(WhileStmt n, Void arg) {
                super.visit(n, arg);
                var original = n.clone();
                ForStmt forStmt = new ForStmt(NodeList.nodeList(),
                                              n.getCondition(),
                                              NodeList.nodeList(),
                                              n.getBody());
                n.replace(forStmt);

                mutations.add(MutatedMethod.from(method));

                forStmt.replace(original);
            }
        }, null);

        return mutations;
    }
}
