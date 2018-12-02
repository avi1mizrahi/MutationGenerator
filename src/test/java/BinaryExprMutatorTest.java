import org.junit.jupiter.api.Test;

class BinaryExprMutatorTest extends MutatorTest {

    @Test
    void flipIntegers() {
        checkExpectation(new MutationsCase(
                "int foo() { return 355 + 123; }",
                "int foo() { return 123 + 355; }"));
    }


    @Test
    void notCommutative() {
        checkExpectation(new MutationsCase(
                "int foo() { return 3 - 11 * 4 / 5; }",
                "int foo() { return 3 - 4 * 11 / 5; }"));
    }

    @Test
    void manyStatements() {
        checkExpectation(new MutationsCase(
                "int foo() { if (a > 5) return a ^ 4;" +
                                      "if (b >= 6 || c < 4) return b - a; }",
                "int foo() { if (5 < a) return a ^ 4;" +
                                      "if (b >= 6 || c < 4) return b - a; }",
                "int foo() { if (a > 5) return 4 ^ a;" +
                                      "if (b >= 6 || c < 4) return b - a; }",
                "int foo() { if (a > 5) return a ^ 4;" +
                                      "if (6 <= b || c < 4) return b - a; }",
                "int foo() { if (a > 5) return a ^ 4;" +
                                      "if (b >= 6 || 4 > c) return b - a; }",
                "int foo() { if (a > 5) return a ^ 4;" +
                                      "if (c < 4 || b >= 6) return b - a; }"));
    }

    @Test
    void dontFlipStringLiterals() {
        checkExpectation(new MutationsCase(
                "int foo() { return \"355\" + (5 + 6) + \"123\"; }",
                "int foo() { return \"355\" + (6 + 5) + \"123\"; }"));
    }

    @Test
    void dontFlipReturnedStrings() {
        checkExpectation(new MutationsCase(
                "int foo() { return simpleGetNameNow() + (6 + 5); }",
                "int foo() { return simpleGetNameNow() + (5 + 6); }"));
    }

    @Test
    void flipReturnedNotStrings() {
        checkExpectation(new MutationsCase(
                "int foo() { return 5 + simpleGetIntNow(); }",
                "int foo() { return simpleGetIntNow() + 5; }"));
    }

    @Test
    void comparingStrings() {
        checkExpectation(new MutationsCase(
                "int foo() { return thisIsString() != null && \"this too\".isEmpty(); }",
                "int foo() { return null != thisIsString() && \"this too\".isEmpty(); }",
                "int foo() { return \"this too\".isEmpty() && thisIsString() != null; }"));
    }

    @Test
    void twoLevel() {
        checkExpectation(new MutationsCase(
                "int foo() { return 5 + 6 * 30 + 14; }",
                "int foo() { return 6 * 30 + 5 + 14; }",
                "int foo() { return 5 + 30 * 6 + 14; }",
                "int foo() { return 14 + 5 + 6 * 30; }"));
    }

    @Override
    MethodMutationProcessor createProcessor() {
        return new BinaryExprMutator();
    }
}