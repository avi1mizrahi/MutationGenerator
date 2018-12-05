import org.junit.jupiter.api.Test;


class RenameMutatorTest extends MutatorTest {
    @Test
    void simpleRename() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                "void foo() { int a = 3; double d; return a + d; }",
                "void foo() { int a0 = 3; double d; return a0 + d; }",
                "void foo() { int a1 = 3; double d; return a1 + d; }",
                "void foo() { int a = 3; double d0; return a + d0; }",
                "void foo() { int a = 3; double d1; return a + d1; }"
        ));
    }

    @Test
    void withObjects() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                "void foo() { A a = 3; double d; return a.foo().d + d + foo().bar() + a.d; }",
                "void foo() { A a0 = 3; double d; return a0.foo().d + d + foo().bar() + a0.d; }",
                "void foo() { A a1 = 3; double d; return a1.foo().d + d + foo().bar() + a1.d; }",
                "void foo() { A a = 3; double d0; return a.foo().d + d0 + foo().bar() + a.d; }",
                "void foo() { A a = 3; double d1; return a.foo().d + d1 + foo().bar() + a.d; }"
        ));
    }

    @Test
    void withFields() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.AS_IS,
               "class C { int i=4, a=84, d0=80, d1=49;" +
                       "void foo() { int a = 3; double d; return a + d + i + d1; }}",
               "void foo() { int a0 = 3; double d; return a0 + d + i + d1; }",
               "void foo() { int a1 = 3; double d; return a1 + d + i + d1; }",
               "void foo() { int a = 3; double d0; return a + d0 + i + d1; }"
        ));
    }

    @Test
    void avoidConflict() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                "void foo() { int a; double a0; return a + a0; }",
                "void foo() { int a1; double a0; return a1 + a0; }",
                "void foo() { int a; double a00; return a + a00; }",
                "void foo() { int a; double a01; return a + a01; }"
        ));
    }

    @Test
    void methodOnVariable() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                "void foo() { int a; double d; return a.foo() + d; }",
                "void foo() { int a0; double d; return a0.foo() + d; }",
                "void foo() { int a1; double d; return a1.foo() + d; }",
                "void foo() { int a; double d0; return a.foo() + d0; }",
                "void foo() { int a; double d1; return a.foo() + d1; }"
        ));
    }

    @Test
    void nestedOperator() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                "public int foo() {"    +
                "    int local2 = 42;"  +
                "    int b = 23;"       +
                "    return 43 + local2 * (b + 1) - b / 33;" +
                "}",
                "public int foo() {"    +
                "    int local20 = 42;"  +
                "    int b = 23;"       +
                "    return 43 + local20 * (b + 1) - b / 33;" +
                "}",
                "public int foo() {"    +
                "    int local21 = 42;"  +
                "    int b = 23;"       +
                "    return 43 + local21 * (b + 1) - b / 33;" +
                "}",
                "public int foo() {"    +
                "    int local2 = 42;"  +
                "    int b0 = 23;"       +
                "    return 43 + local2 * (b0 + 1) - b0 / 33;" +
                "}",
                "public int foo() {"    +
                "    int local2 = 42;"  +
                "    int b1 = 23;"       +
                "    return 43 + local2 * (b1 + 1) - b1 / 33;" +
                "}"));
    }

    @Override
    MethodMutationProcessor createProcessor() {
        return new RenameMutator(new StupidNameGenerator(2));
    }
}