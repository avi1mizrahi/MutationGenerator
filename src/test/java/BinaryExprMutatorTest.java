import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BinaryExprMutatorTest {

    static private class MutationsCase {
        private final MethodDeclaration orig;
        private final Set<MethodDeclaration> expected;

        MutationsCase(String from, String... to) {
            var cu1 = JavaParser.parse(from);
            var cus = List.of(to).stream().map(JavaParser::parse).collect(Collectors.toSet());

            orig = cu1.findFirst(MethodDeclaration.class).get();
            expected = cus.stream().map(cu -> cu.findFirst(MethodDeclaration.class).get() ).collect(Collectors.toSet());
        }

        public Set<String> expectedAsStrings() {
            return expected.stream().map(Node::toString).collect(Collectors.toSet());
        }
    }

    private static void checkExpectation(MutationsCase mutationsCase) {
        var binaryExprMutator = new BinaryExprMutator();
        var generatedMutations = Set.copyOf(binaryExprMutator.process(mutationsCase.orig));

        assertEquals(mutationsCase.expected.size(), generatedMutations.size());

        assertFalse(generatedMutations.contains(mutationsCase.orig.toString()));
        assertEquals(mutationsCase.expectedAsStrings(), generatedMutations);
    }

    @Test
    void flipIntegers() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { return 355 + 123; } }",
                "class C { int foo() { return 123 + 355; } }"));
    }


    @Test
    void notCommutative() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { return 3 - 11 * 4 / 5; } }",
                "class C { int foo() { return 3 - 4 * 11 / 5; } }"));
    }

    @Test
    void manyStatements() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { if (a > 5) return a ^ 4;" +
                                      "if (b >= 6 || c < 4) return b - a; } }",
                "class C { int foo() { if (5 < a) return a ^ 4;" +
                                      "if (b >= 6 || c < 4) return b - a; } }",
                "class C { int foo() { if (a > 5) return 4 ^ a;" +
                                      "if (b >= 6 || c < 4) return b - a; } }",
                "class C { int foo() { if (a > 5) return a ^ 4;" +
                                      "if (6 <= b || c < 4) return b - a; } }",
                "class C { int foo() { if (a > 5) return a ^ 4;" +
                                      "if (b >= 6 || 4 > c) return b - a; } }",
                "class C { int foo() { if (a > 5) return a ^ 4;" +
                                      "if (c < 4 || b >= 6) return b - a; } }"));
    }

    @Test
    void dontFlipStringLiterals() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { return \"355\" + (5 + 6) + \"123\"; } }",
                "class C { int foo() { return \"355\" + (6 + 5) + \"123\"; } }"));
    }

    @Test
    void dontFlipReturnedStrings() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { return simpleGetNameNow() + (6 + 5); } }",
                "class C { int foo() { return simpleGetNameNow() + (5 + 6); } }"));
    }

    @Test
    void flipReturnedNotStrings() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { return 5 + simpleGetIntNow(); } }",
                "class C { int foo() { return simpleGetIntNow() + 5; } }"));
    }

    @Test
    void comparingStrings() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { return thisIsString() != null && \"this too\".isEmpty(); } }",
                "class C { int foo() { return null != thisIsString() && \"this too\".isEmpty(); } }",
                "class C { int foo() { return \"this too\".isEmpty() && thisIsString() != null; } }"));
    }

    @Test
    void twoLevel() {
        checkExpectation(new MutationsCase(
                "class C { int foo() { return 5 + 6 * 30 + 14; } }",
                "class C { int foo() { return 6 * 30 + 5 + 14; } }",
                "class C { int foo() { return 5 + 30 * 6 + 14; } }",
                "class C { int foo() { return 14 + 5 + 6 * 30; } }"));
    }
}