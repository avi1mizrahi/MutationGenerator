import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

interface NameGenerator {
    List<String> generateNames(String name);
}

/**
 * Currently this mutator is "stupid" in the sense that it doesn't understand the variable scoping
 * semantics, so it just use a simple safe find-and-replace: - 100% precision, i.e. if a rename took
 * place, the semantics is preserved - Some renames will be missed as may be valid because of
 * variable/field hiding etc.
 */
public class RenameMutator implements MethodMutationProcessor {
    private final NameGenerator nameGenerator;

    RenameMutator(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    private static void renameAll(List<SimpleName> allNames, SimpleName from, SimpleName to) {
        allNames.forEach(simpleName -> {
            if (simpleName.equals(from)) {
                simpleName.setIdentifier(to.getIdentifier());
            }
        });
    }

    @Override
    public List<String> process(MethodDeclaration method) {
        List<String> mutations = new ArrayList<>();

        final List<SimpleName> allNames = method.findAll(NameExpr.class)
                                                .stream()
                                                .map(NameExpr::getName)
                                                .collect(Collectors.toList());

        for (var declarator : method.findAll(VariableDeclarator.class)) {

            nameGenerator.generateNames(declarator.getNameAsString())
                         .stream()
                         .map(SimpleName::new)
                         .filter(not(allNames::contains))
                         .forEach(suggestedName -> {

                final var oldName = declarator.getName();

                declarator.setName(suggestedName);
                renameAll(allNames, oldName, suggestedName);

                mutations.add(method.toString());

                renameAll(allNames, suggestedName, oldName);
                declarator.setName(oldName);
            });
        }

        return mutations;
    }

    static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
