import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;

import java.util.ArrayList;
import java.util.List;

interface NameGenerator {
    List<String> generateNames(String name);
}

/**
 * Currently this mutator is "stupid" in the sense that it doesn't understand
 * the variable scoping semantics, so it just use a simple safe find-and-replace:
 * - 100% precision, i.e. if a rename took place, the semantics is preserved
 * - Some renames will be missed as may be valid because of variable/field hiding etc.
 */
public class RenameMutator implements MethodMutationGenerator {
    private final NameGenerator nameGenerator;

    RenameMutator(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    private static void rename(List<SimpleName> allNames, String from, String to) {
        final var fromSimple = new SimpleName(from);
        allNames.forEach(simpleName -> {
            if (simpleName.equals(fromSimple)) {
                simpleName.setIdentifier(to);
            }
        });
    }

    @Override
    public List<String> process(MethodDeclaration method) {
        List<String> mutations = new ArrayList<>();

        final List<SimpleName> allNames = method.findAll(SimpleName.class);

        for (var declarator : method.findAll(VariableDeclarator.class)) {

            var suggestedNames = nameGenerator.generateNames(declarator.getNameAsString());
            for (var suggestedName : suggestedNames) {
                if (allNames.contains(new SimpleName(suggestedName)))
                    continue;

                var oldName = declarator.getName().asString();

                rename(allNames, oldName, suggestedName);
                mutations.add(method.toString());
                rename(allNames, suggestedName, oldName);
            }
        }

        return mutations;
    }
}
