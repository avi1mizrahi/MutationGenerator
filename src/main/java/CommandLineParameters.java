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

        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            throw e;
        }
    }

    private void checkInputDirectory() throws CmdLineException {
        if (!inputDirectory.exists() || !inputDirectory.isDirectory())
            throw new CmdLineException(parser, "no such input directory", new Throwable());
    }

    private void checkOutputDirectory() throws CmdLineException {
        if (outputDirectory.exists()) {
            if (!outputDirectory.isDirectory())
                throw new CmdLineException(parser, "no such output directory", new Throwable());
        } else {
            if (!outputDirectory.mkdirs())
                throw new CmdLineException(parser, "can't create output directory", new Throwable());
        }
    }
}
