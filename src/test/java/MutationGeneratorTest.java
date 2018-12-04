import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class MutationGeneratorTest {
    private static final String INPUT_DIR = "src/test/cases";
    private static final String OUTPUT_DIR = "src/test/outputs";
    private static final String EXPECTED_OUTPUT_DIR = "src/test/expected";

    @BeforeEach
    void setUp() {
        deleteOutputDir();
    }

    private static void deleteOutputDir() {
        try {
            FileUtils.deleteDirectory(new File(OUTPUT_DIR));
        } catch (IOException ignored) {
        }
    }

    static void assertDirectoriesEqual(Path dir1, Path dir2) throws Exception {
        var i1 = Files.walk(dir1).iterator();
        final Exception[] exception = {null};

        Files.walk(dir2).forEach(path -> {
            if (dir2.toFile().isDirectory()) return;
            try {
                assertArrayEquals(Files.readAllBytes(i1.next()), Files.readAllBytes(path));
            } catch (IOException e) {
                exception[0] = e;
            }
        });

        if (exception[0] != null) throw exception[0];
    }

    @Test
    void test() {
    // TODO: two executions, until the bug will be fixed
    //  (that the mutations should not be used together)
        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--flip-binary-expr"
                }));

        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--rename-variable",
                        "--num-similarities", "2"
                }));

        assertDoesNotThrow(() ->
                assertDirectoriesEqual(Paths.get(OUTPUT_DIR), Paths.get(EXPECTED_OUTPUT_DIR)));
    }
}