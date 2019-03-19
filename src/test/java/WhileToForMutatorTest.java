import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;

class WhileToForMutatorTest extends MutatorTest {
    @Test
    void simpleWhile() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                                           "int foo(int a) { while (a != 3) a++; return a;}",
                                           "int foo(int a) { for (;a != 3;) a++; return a;}"
        ));
    }

    @Test
    void multipleWhiles() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                                           "int foo(int a) { while (a != 3) a++; while (a == b) {b++;} return a;}",
                                           "int foo(int a) { for (;a != 3;) a++; while (a == b) {b++;} return a;}",
                                           "int foo(int a) { while (a != 3) a++; for (;a == b;) {b++;} return a;}"
        ));
    }

    @Test
    void nestedWhiles() {
        checkExpectation(new MutationsCase(MutationsCase.WrapOptions.WRAP_WITH_CLASS,
                                           "int foo(int a) { while (a != 3) while (a == b) {b++;} return a;}",
                                           "int foo(int a) { for (;a != 3;) while (a == b) {b++;} return a;}",
                                           "int foo(int a) { while (a != 3) for (;a == b;) {b++;} return a;}"
        ));
    }

    @Override
    MutationProcessor<MethodDeclaration> createProcessor() {
        return new WhileToForMutator();
    }

}