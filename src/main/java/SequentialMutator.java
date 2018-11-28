import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

interface MethodMutationGenerator {
    List<String> process(MethodDeclaration method);
}

class SequentialMutator {
    private final MethodMutationGenerator mutator;

    SequentialMutator(MethodMutationGenerator mutator) {
        this.mutator = mutator;
    }

    List<String> process(CompilationUnit compilationUnit) {
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
}
