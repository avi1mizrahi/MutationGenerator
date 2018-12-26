import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

class SequentialMutationProcessor implements MutationProcessor<CompilationUnit> {
    private final MutationProcessor<MethodDeclaration> mutator;

    SequentialMutationProcessor(MutationProcessor<MethodDeclaration> mutator) {
        this.mutator = mutator;
    }

    @Override
    public List<String> process(CompilationUnit compilationUnit) {
        final ArrayList<String> mutations = new ArrayList<>();

        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                super.visit(n, arg);

                mutations.addAll(mutator.process(n));
            }
        }, null);

        return mutations;
    }

    @Override
    public String toString() {
        return mutator.getClass().getSimpleName();
    }
}
