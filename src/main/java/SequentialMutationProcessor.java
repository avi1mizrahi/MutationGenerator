import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class SequentialMutationProcessor implements MutationProcessor<CompilationUnit> {
    private final MutationProcessor<MethodDeclaration> mutator;

    private final boolean outputOriginalMethod;
    private final boolean skipOriginalIfEmpty;
    private final int     maxMutationsPerMethod;

    SequentialMutationProcessor(MutationProcessor<MethodDeclaration> mutator) {
        this(mutator, false, false, 0);
    }

    SequentialMutationProcessor(MutationProcessor<MethodDeclaration> mutator,
                                boolean outputOriginalMethod,
                                boolean skipOriginalIfEmpty, int maxMutationsPerMethod) {
        this.mutator = mutator;
        this.outputOriginalMethod = outputOriginalMethod;
        this.skipOriginalIfEmpty = skipOriginalIfEmpty;
        this.maxMutationsPerMethod = maxMutationsPerMethod;
    }

    private static void index(List<MutatedMethod> mutations) {
        int i = 0;
        for (MutatedMethod mutation : mutations) {
            mutation.setIndex(++i);
        }
    }

    @Override
    public List<MutatedMethod> process(CompilationUnit compilationUnit) {
        final ArrayList<MutatedMethod> mutations = new ArrayList<>();

        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                super.visit(n, arg);

                List<MutatedMethod> res = mutator.process(n);
                index(res);
                if (maxMutationsPerMethod > 0 && maxMutationsPerMethod < res.size()) {
                    Collections.shuffle(res);
                    res = res.stream().limit(maxMutationsPerMethod).collect(Collectors.toList());
                }
                if (outputOriginalMethod && !(skipOriginalIfEmpty && res.isEmpty())) {
                    res.add(MutatedMethod.from(n));
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
