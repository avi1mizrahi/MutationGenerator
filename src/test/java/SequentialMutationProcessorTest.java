import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SequentialMutationProcessorTest {
    private static final List<String>    STRINGS = Arrays.asList("Hi",
                                                                 "Bye",
                                                                 "die",
                                                                 "lie");
    private static final  String          CODE    = "class C { void v() {} }";
    private              CompilationUnit unit;

    @BeforeEach
    void setUp() {
        unit = JavaParser.parse(CODE);
    }

    @Test
    void process() {
        var seq = new SequentialMutationProcessor(
                compilationUnit -> STRINGS.stream()
                                          .map(s -> MutatedMethod.from("v", s))
                                          .collect(Collectors.toList()));

        List<MutatedMethod> res = seq.process(unit);

        assertEquals(STRINGS, toListOfCodeString(res));
    }

    private List<String> toListOfCodeString(List<MutatedMethod> res) {
        return res.stream().map(MutatedMethod::getCode).collect(Collectors.toList());
    }

    @Test
    void limit() {
        var seq = new SequentialMutationProcessor(
                compilationUnit -> STRINGS.stream()
                                          .map(s -> MutatedMethod.from("v", s))
                                          .collect(Collectors.toList()),
                false,
                false,
                2);

        List<MutatedMethod> res = seq.process(unit);

        assertEquals(2, res.size());
        assertTrue(Set.copyOf(STRINGS).containsAll(toListOfCodeString(res)));
    }

    @Test
    void outputOriginal() {
        var seq = new SequentialMutationProcessor(
                compilationUnit -> STRINGS.stream()
                                          .map(s -> MutatedMethod.from("v", s))
                                          .collect(Collectors.toList()),
                true,
                false,
                2);

        List<MutatedMethod> res = seq.process(unit);

        assertEquals(3, res.size());
        int i = res.indexOf(MutatedMethod.from("v", "void v() {\n}"));
        assertNotEquals(-1, i);
        MutatedMethod code = res.remove(i);
        assertTrue(Set.copyOf(STRINGS).containsAll(toListOfCodeString(res)));
    }

    @Test
    void outputOriginalWhenEmpty() {
        var seq = new SequentialMutationProcessor(
                compilationUnit -> new ArrayList<>(),
                true,
                false,
                2);

        List<MutatedMethod> res = seq.process(unit);

        assertEquals(1, res.size());
        int i = res.indexOf(MutatedMethod.from("v", "void v() {\n}"));
        assertNotEquals(-1, i);
    }

    @Test
    void outputOriginalIfEmptyWhenEmpty() {
        var seq = new SequentialMutationProcessor(
                compilationUnit -> List.of(),
                true,
                true,
                2);

        List<MutatedMethod> res = seq.process(unit);

        assertTrue(res.isEmpty());
    }

    @Test
    void outputOriginalIfEmptyWhenNotEmpty() {
        var seq = new SequentialMutationProcessor(
                compilationUnit -> STRINGS.stream()
                                          .map(s -> MutatedMethod.from("v", s))
                                          .collect(Collectors.toList()),
                true,
                true,
                2);

        List<MutatedMethod> res = seq.process(unit);

        assertEquals(3, res.size());
        int i = res.indexOf(MutatedMethod.from("v", "void v() {\n}"));
        assertNotEquals(-1, i);
        MutatedMethod code = res.remove(i);
        assertTrue(Set.copyOf(STRINGS).containsAll(toListOfCodeString(res)));
    }

    @Test
    void limit_empty() {
        var seq = new SequentialMutationProcessor(compilationUnit -> new ArrayList<>(),
                                                  false,
                                                  false,
                                                  2);

        List<MutatedMethod> res = seq.process(unit);

        assertTrue(res.isEmpty());
    }
}