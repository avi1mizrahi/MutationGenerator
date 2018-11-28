import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineParametersTest {
    private static final String BAD_PATH = "src/test/cases/no_such_file_11123f.java";
    private static final String NOT_A_DIR = "src/test/cases/T1.java";
    private static final String INPUT_DIR = "src/test/cases/";
    private static final String OUTPUT_DIR = "src/test/outputs";

    private static void deleteOutputDir() {
        try {
            FileUtils.deleteDirectory(new File(OUTPUT_DIR));
        } catch (IOException ignored) {
        }
    }

    @BeforeEach
    void setUp() {
        deleteOutputDir();
    }

    @Test
    void badPaths() {
        var exception = assertThrows(CmdLineException.class,
                () -> new CommandLineParameters(
                        "--input-dir", BAD_PATH,
                        "--output-dir", INPUT_DIR
                ));
        assertEquals("no such input directory", exception.getMessage());

        exception = assertThrows(CmdLineException.class,
                () -> new CommandLineParameters(
                        "--input-dir", NOT_A_DIR,
                        "--output-dir", INPUT_DIR
                ));
        assertEquals("no such input directory", exception.getMessage());

        exception = assertThrows(CmdLineException.class,
                () -> new CommandLineParameters(
                        "--input-dir", INPUT_DIR,
                        "--output-dir", NOT_A_DIR));
        assertEquals("no such output directory", exception.getMessage());
    }

    @Test
    void goodPaths() {
        assertDoesNotThrow(() ->
                new CommandLineParameters(
                        "--input-dir", INPUT_DIR,
                        "--output-dir", OUTPUT_DIR));
    }
}