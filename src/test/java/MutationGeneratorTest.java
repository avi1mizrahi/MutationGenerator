import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

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
        Set<Path> paths1 = new HashSet<>();
        Set<Path> paths2 = new HashSet<>();

        try (var s1 = Files.walk(dir1);
             var s2 = Files.walk(dir2)) {

            var i1 = s1.iterator();
            var i2 = s2.iterator();

            i1.next(); i2.next();//skip the root

            while (i1.hasNext() && i2.hasNext()) {
                paths1.add(dir1.relativize(i1.next()));
                paths2.add(dir2.relativize(i2.next()));
            }
        }

        for (var path : paths1) {
            assertTrue(paths2.remove(path), path.toString());

            Path file1 = dir1.resolve(path);
            Path file2 = dir2.resolve(path);

            if (file1.toFile().isDirectory()) {
                assertTrue(file2.toFile().isDirectory());
                continue;
            }

            assertArrayEquals(Files.readAllBytes(file1),
                              Files.readAllBytes(file2),
                              () -> "Difference:\nfile1:" + file1 + "\nfile2:" + file2 + '\n');
        }

        assertTrue(paths2.isEmpty(), () -> paths2.iterator().next().toString());
    }

    @Test
    void test() {
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
               MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--invert-if-else"
                }));

        assertDoesNotThrow(() ->
                assertDirectoriesEqual(Paths.get(OUTPUT_DIR), Paths.get(EXPECTED_OUTPUT_DIR + "/basic")));
    }

    @Test
    void outputOriginal() {
        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--output-original",
                        "--flip-binary-expr"
                }));

        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--output-original",
                        "--rename-variable",
                        "--num-similarities", "2"
                }));

        assertDoesNotThrow(() ->
               MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                       "--output-original",
                        "--invert-if-else"
                }));

        assertDoesNotThrow(() ->
                assertDirectoriesEqual(Paths.get(OUTPUT_DIR), Paths.get(EXPECTED_OUTPUT_DIR + "/outputOriginal")));
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

        assertDoesNotThrow(() ->
               MutationGenerator.main(new String[]{
                        "--num-threads", "1",
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR,
                        "--invert-if-else"
                }));

        assertDoesNotThrow(() -> assertDirectoriesEqual(Paths.get(OUTPUT_DIR),
                                                        Paths.get(EXPECTED_OUTPUT_DIR + "/basic")));
    }
}