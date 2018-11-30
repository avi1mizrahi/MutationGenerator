import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinaryExprMutatorTest {

    @Test
    void process() {
        var cu = new CompilationUnit();
        var type1 = cu.addClass("C1");
        var type2 = cu.addClass("C2");
        var foo = type1.addMethod("foo", Modifier.PUBLIC);
        var foo2 = type2.addMethod("foo", Modifier.PUBLIC);

        foo.setBody(new BlockStmt());
        foo2.setBody(new BlockStmt());

        var binaryExp = new BinaryExpr(new IntegerLiteralExpr(34), new IntegerLiteralExpr(88), BinaryExpr.Operator.MULTIPLY);
        foo.getBody().get().addStatement(new ReturnStmt(binaryExp));

        var mutatedBinaryExp = new BinaryExpr(new IntegerLiteralExpr(88), new IntegerLiteralExpr(34), BinaryExpr.Operator.MULTIPLY);
        foo2.getBody().get().addStatement(new ReturnStmt(mutatedBinaryExp));

        var binaryExprMutator = new BinaryExprMutator();
        List<String> m = binaryExprMutator.process(foo);

        assertEquals(1, m.size());

        assertNotEquals(foo.toString(), m.get(0));
        assertEquals(foo2.toString(), m.get(0));
    }
}