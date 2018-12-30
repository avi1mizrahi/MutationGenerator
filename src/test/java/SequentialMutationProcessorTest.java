import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SequentialMutationProcessorTest {
    private static final List<String>    STRINGS = Arrays.asList("Hi",
                                                                 "Bye",
                                                                 "die",
                                                                 "lie");
    public static final  String          CODE    = "class C { void v() {} }";
    private              CompilationUnit unit;

    @BeforeEach
    void setUp() {
        unit = JavaParser.parse(CODE);
    }

    @Test
    void process() {
        var seq = new SequentialMutationProcessor(compilationUnit -> new ArrayList<>(STRINGS));

        List<String> res = seq.process(unit);

        assertEquals(STRINGS, res);
    }

    @Test
    void limit() {
        var seq = new SequentialMutationProcessor(compilationUnit -> new ArrayList<>(STRINGS), 2);

        List<String> res = seq.process(unit);

        assertEquals(2, res.size());
        assertTrue(Set.copyOf(STRINGS).containsAll(res));
    }

    @Test
    void limit_empty() {
        var seq = new SequentialMutationProcessor(compilationUnit -> new ArrayList<>(), 2);

        List<String> res = seq.process(unit);

        assertTrue(res.isEmpty());
    }
}