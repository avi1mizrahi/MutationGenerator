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

    static void assertDirectoriesEqual(Path dir1, Path dir2) throws IOException {
        try (var s1 = Files.walk(dir1);
             var s2 = Files.walk(dir2)) {

            var i1 = s1.iterator();
            var i2 = s2.iterator();

            i1.next(); i2.next();//skip the root

            while (i1.hasNext() && i2.hasNext()) {
                var p1 = i1.next();
                var p2 = i2.next();

                assertEquals(p1.getFileName(), p2.getFileName());

                if (p1.toFile().isDirectory()) {
                    assertTrue(p2.toFile().isDirectory());
                    continue;
                }

                assertArrayEquals(Files.readAllBytes(p1), Files.readAllBytes(p2));
            }

            assertFalse(i1.hasNext());
            assertFalse(i2.hasNext());
        }
    }

    @Test
    void test() {
    // TODO: two executions, until the bug will be fixed
    //  (that the mutations should not be used together)
        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--flip-binary-expr"
                }));

        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--rename-variable",
                        "--num-similarities", "2"
                }));

        assertDoesNotThrow(() ->
                assertDirectoriesEqual(Paths.get(OUTPUT_DIR), Paths.get(EXPECTED_OUTPUT_DIR)));
    }
    @Test
    void testWithCache() {
        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--flip-binary-expr"
                }));

        assertDoesNotThrow(() ->
               MutationGenerator.main(new String[]{
                       "--num-threads", "1",
                       "--input-dir", INPUT_DIR,
                       "--output-dir", OUTPUT_DIR,
                       "--rename-variable",
                       "--rename-cache-size", "3",
                       "--num-similarities", "2"
               }));

        assertDoesNotThrow(() -> assertDirectoriesEqual(Paths.get(OUTPUT_DIR),
                                                        Paths.get(EXPECTED_OUTPUT_DIR)));
    }
}