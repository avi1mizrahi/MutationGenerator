import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;


class IfElseMutatorTest extends MutatorTest {
    @Test
    void simpleIf() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                                           "int foo(int a) { if (a != 3) return 2; else a = 43; return a;}",
                                           "int foo(int a) { if (a == 3) a = 43; else return 2; return a;}"
        ));
    }

    @Test
    void leq() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                                           "int foo(int a) { if (a <= 3) return 2; else a = 43; return a;}",
                                           "int foo(int a) { if (a > 3) a = 43; else return 2; return a;}"
        ));
    }

    @Test
    void le() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                                           "int foo(int a) { if (a < 3) return 2; else a = 43; return a;}",
                                           "int foo(int a) { if (a >= 3) a = 43; else return 2; return a;}"
        ));
    }

    @Test
    void manyIfs() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
               "int foo(int a) { " +
                       "if (a != 3) {return 2;} else a = 43; return a;" +
                       "if (a >= 4) {a=3; return a;} else {a = 423;} return a;" +
                       "}",
               "int foo(int a) { " +
                       "if (a == 3) a = 43; else {return 2;} return a;" +
                       "if (a >= 4) {a=3; return a;} else {a = 423;} return a;" +
                       "}",
               "int foo(int a) { " +
                       "if (a != 3) {return 2;} else a = 43; return a;" +
                       "if (a < 4) {a = 423;} else {a=3; return a;} return a;" +
                       "}"
        ));
    }

    @Test
    void ifElseIf() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
               "int foo(int a) { " +
                       "if (a > 3) {return 2;} " +
                       "else if (a == 4) {a=3; return a;} else {a = 423;} return a;" +
                       "}",
               "int foo(int a) { " +
                       "if (a <= 3) if (a == 4) {a=3; return a;} else {a = 423;}" +
                       "else {return 2;} return a;" +
                       "}",
               "int foo(int a) { " +
                       "if (a > 3) {return 2;} " +
                       "else if (a != 4) {a = 423;} else {a=3; return a;} return a;" +
                       "}"
        ));
    }

    @Override
    MutationProcessor<MethodDeclaration> createProcessor() {
        return new IfElseMutator();
    }
}