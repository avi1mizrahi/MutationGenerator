import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

abstract class MutatorTest {
    abstract MutationProcessor<MethodDeclaration> createProcessor();

    static class MutationsCase {
        private final MethodDeclaration orig;
        private final Set<MethodDeclaration> expected;

        enum WrapOptions {WRAP_WITH_CLASS, AS_IS}

        MutationsCase(WrapOptions wrapOptions, String from, String... to) {
            if (wrapOptions == WrapOptions.WRAP_WITH_CLASS) {
                from = wrapWithClass(from);
            }
            var cu1 = JavaParser.parse(from);
            var cus = List.of(to).stream().map(MutationsCase::wrapWithClass)
                          .map(JavaParser::parse).collect(Collectors.toSet());

            orig = cu1.findFirst(MethodDeclaration.class).get();
            expected = cus.stream().map(cu -> cu.findFirst(MethodDeclaration.class).get() ).collect(Collectors.toSet());
        }

        Set<String> expectedAsStrings() {
            return expected.stream().map(Node::toString).collect(Collectors.toSet());
        }

        static String wrapWithClass(String method) {
            return String.format("class C {%s}", method);
        }
    }

    void checkExpectation(MutationsCase mutationsCase) {
        var processor = createProcessor();
        var generatedMutations = processor.process(mutationsCase.orig)
                                          .stream()
                                          .map(MutatedMethod::getCode)
                                          .collect(Collectors.toSet());

        assertEquals(mutationsCase.expected.size(), generatedMutations.size());

        assertFalse(generatedMutations.contains(mutationsCase.orig.toString()));
        assertEquals(mutationsCase.expectedAsStrings(), generatedMutations);
    }
}
