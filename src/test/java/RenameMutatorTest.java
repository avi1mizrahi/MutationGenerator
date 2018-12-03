import org.junit.jupiter.api.Test;


class RenameMutatorTest extends MutatorTest {
    @Test
    void simpleRename() {
        checkExpectation(new MutationsCase(
                "void foo() { int a; double d; return a + d; }",
                "void foo() { int a0; double d; return a0 + d; }",
                "void foo() { int a1; double d; return a1 + d; }",
                "void foo() { int a; double d0; return a + d0; }",
                "void foo() { int a; double d1; return a + d1; }"
        ));
    }

    @Test
    void avoidConflict() {
        checkExpectation(new MutationsCase(
                "void foo() { int a; double a0; return a + a0; }",
                "void foo() { int a1; double a0; return a1 + a0; }",
                "void foo() { int a; double a00; return a + a00; }",
                "void foo() { int a; double a01; return a + a01; }"
        ));
    }

    @Test
    void methodOnVariable() {
        checkExpectation(new MutationsCase(
                "void foo() { int a; double d; return a.foo() + d; }",
                "void foo() { int a0; double d; return a0.foo() + d; }",
                "void foo() { int a1; double d; return a1.foo() + d; }",
                "void foo() { int a; double d0; return a.foo() + d0; }",
                "void foo() { int a; double d1; return a.foo() + d1; }"
        ));
    }

    @Override
    MethodMutationProcessor createProcessor() {
        return new RenameMutator(new StupidNameGenerator(2));
    }
}