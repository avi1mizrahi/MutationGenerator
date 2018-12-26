import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;

class CommandLineParameters {

    private final CmdLineParser parser;

    @Option(name = "--help", aliases = "-h", usage = "show this message")
    private boolean help;

    @Option(name = "--input-dir", metaVar = "DIR", required = true, usage = "Directory with Java files. Source will be found recursively")
    public File inputDirectory = null;

    @Option(name = "--output-dir", metaVar = "DIR", required = true, usage = "output directory, generated files will be found here.")
    public File outputDirectory = null;

    @Option(name = "--flip-binary-expr", usage = "Flips binary expressions. \nExamples:\n 'obj + elm' -> 'elm + obj'\n 'obj <= elm' -> 'elm >= obj', ")
    public boolean flipBinaryExpr = false;

    @Option(name = "--rename-variable", usage = "Rename local variable")
    public boolean renameVariable = false;

    @Option(name = "--rename-cache-size", depends = "--rename-variable", usage = "Cache size (elements), to store generated similarities. 0 -> no cache")
    public int renameCacheSize = 0;

    @Option(name = "--word2vec-map", depends = "--rename-variable", usage = "word embeddings file")
    public File word2vecMap = null;

    @Option(name = "--num-similarities", depends = "--rename-variable", usage = "number of similarities to find for each word")
    public int numSimilarities = 3;

    @Option(name = "--num-threads")
    public int numThreads = 8;


    public CommandLineParameters(String... args) throws CmdLineException {
        parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
            if (help) {
                parser.printUsage(System.out);
                System.exit(0);//TODO find a better way?
            }

            checkInputDirectory();
            checkOutputDirectory();

            checkMutations();
            checkThreads();

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            throw e;
        }
    }

    private void checkThreads() throws CmdLineException {
        if (numThreads < 1) error("Don't be funny, --num-threads must be positive");
    }

    private void checkMutations() throws CmdLineException {
        if (!flipBinaryExpr && !renameVariable) {
            error("No mutation was chosen");
        }

        if (flipBinaryExpr && renameVariable) {//TODO fixme
            error("Forbidden due to a known bug");
        }

        checkRenameVariable();
    }

    private void checkRenameVariable() throws CmdLineException {
        if (!renameVariable) {
            return;
        }

        if (numSimilarities < 1) error("numSimilarities must be positive");
        if (word2vecMap != null && !word2vecMap.exists()) error("No such \"word2vecMap\": " + word2vecMap.getPath());
    }

    private void checkInputDirectory() throws CmdLineException {
        if (!inputDirectory.exists() || !inputDirectory.isDirectory())
            error("No such input directory");
    }

    private void checkOutputDirectory() throws CmdLineException {
        if (outputDirectory.exists()) {
            if (!outputDirectory.isDirectory())
                error("No such output directory");
        } else {
            if (!outputDirectory.mkdirs())
                error("can't create output directory");
        }
    }

    private void error(String message) throws CmdLineException {
        throw new CmdLineException(parser, message, new Throwable());
    }
}
