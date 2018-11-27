import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MutationGeneratorTest {
    public static final String INPUT_DIR = "src/test/cases";
//    public static final String INPUT_DIR = "src/test/cases/temp";
//    public static final String INPUT_DIR = "/Users/mizrahi/Code2VecProject/java-small/training/intellij-community";
    public static final String OUTPUT_DIR = "src/test/outputs";

    @BeforeEach
    void setUp() {
        deleteOutputDir();
    }

    private static void deleteOutputDir() {
        try {
            FileUtils.deleteDirectory(new File(OUTPUT_DIR));
        } catch (IOException e) {
        }
    }

    @Test
    void main2() {
        assertDoesNotThrow(() ->
        MutationGenerator.main(new String[]{
                "--input-dir", INPUT_DIR,
                "--output-dir", OUTPUT_DIR
        }));
    }

    final static String bad_code = "" +
            "class AsId <T extends HasId<? super T>> implements Id<T> {}\n" +
            "interface HasId<T extends HasId<T>> {}\n" +
            "interface Id<T extends HasId<? super T>>{}\n" +
            "\n" +
            "interface Pong<T> {}\n" +
            "class Ping<T> implements Pong<Pong<? super Ping<Ping<T>>>> {\n" +
            "  static void Ping() {\n" +
            "    Pong<? super Ping<Long>> Ping = new Ping<Long>();\n" +
            "  }\n" +
            "}\n";

    @Test
    void main() {
        CompilationUnit parsed = null;
        System.out.println(System.getenv("PWD"));
        try {
            parsed = JavaParser.parse(new File(INPUT_DIR + "/T1.java"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(parsed.findAll(MethodDeclaration.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ALL GOOD");
//		for (Object o : parsed.getParentNodeOfType(MethodContent.class)) {
//			System.out.println(o);
//		}
    }

}