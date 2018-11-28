import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;

class CommandLineParameters {

    private final CmdLineParser parser;
    public File inputDirectory = null;
    public File outputDirectory = null;

    public CommandLineParameters(String... args) throws CmdLineException {
        parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            throw e;
        }
    }

    @Option(name = "--input-dir", required = true,
            usage = "Directory with Java files. Source will be found recursively")
    void setInputDirectory(File directory) throws CmdLineException {
        if (!directory.exists() || !directory.isDirectory())
            throw new CmdLineException(parser, "no such input directory", new Throwable());
        inputDirectory = directory;
    }

    @Option(name = "--output-dir", required = true,
            usage = "output directory, generated files will be found here.")
    void setOutputDirectory(File directory) throws CmdLineException {
        if (directory.exists()) {
            if (!directory.isDirectory())
                throw new CmdLineException(parser, "no such output directory", new Throwable());
        } else {
            if (!directory.mkdirs())
                throw new CmdLineException(parser, "can't create output directory", new Throwable());
        }
        outputDirectory = directory;
    }
}
