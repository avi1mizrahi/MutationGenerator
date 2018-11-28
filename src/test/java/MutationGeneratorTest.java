import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MutationGeneratorTest {
    //    public static final String INPUT_DIR = "src/test/cases";
    private static final String INPUT_DIR = "/Users/mizrahi/Code2VecProject/java-small/training/intellij-community";
    private static final String OUTPUT_DIR = "src/test/outputs";

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

    @Test
    void main() {
        assertDoesNotThrow(() ->
                MutationGenerator.main(new String[]{
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR
                }));
    }

}