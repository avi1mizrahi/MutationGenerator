import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class SequentialMutationProcessor implements MutationProcessor<CompilationUnit> {
    private final MutationProcessor<MethodDeclaration> mutator;
    private final int                                  maxMutationsPerMethod;

    SequentialMutationProcessor(MutationProcessor<MethodDeclaration> mutator) {
        this(mutator, 0);
    }

    SequentialMutationProcessor(MutationProcessor<MethodDeclaration> mutator,
                                int maxMutationsPerMethod) {
        this.mutator = mutator;
        this.maxMutationsPerMethod = maxMutationsPerMethod;
    }

    @Override
    public List<MutatedMethod> process(CompilationUnit compilationUnit) {
        final ArrayList<MutatedMethod> mutations = new ArrayList<>();

        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                super.visit(n, arg);

                List<MutatedMethod> res = mutator.process(n);
                if (maxMutationsPerMethod > 0 && maxMutationsPerMethod < res.size()) {
                    Collections.shuffle(res);
                    res = res.stream().limit(maxMutationsPerMethod).collect(Collectors.toList());
                }

                mutations.addAll(res);
            }
        }, null);

        return mutations;
    }

    @Override
    public String toString() {
        return mutator.getClass().getSimpleName();
    }
}
