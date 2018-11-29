import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class MutationGenerator {

    public static void main(String[] args) throws CmdLineException {
        new MutationGenerator(new CommandLineParameters(args));
    }

    MutationGenerator(CommandLineParameters commandLineParameters) {
        List<CuMutationProcessor> mutators = new ArrayList<>();

        if (commandLineParameters.flipBinaryExpr) {
            mutators.add(new SequentialMutationProcessor(new BinaryExprMutator()));
        }
        if (commandLineParameters.renameVariable) {
            mutators.add(new SequentialMutationProcessor(new RenameMutator(name -> new ArrayList<>(List.of("newName1", "b")))));
        }

        final File[] files = Objects.requireNonNull(commandLineParameters.inputDirectory.listFiles(
                pathname -> pathname.isFile() && pathname.getName().toLowerCase().endsWith(".java")));

        for (var file : files) {
            processFile(mutators, file, commandLineParameters.outputDirectory);
        }
    }

    private static int processFile(List<CuMutationProcessor> mutators, File file, File outputDirectory) {
        int nFailed = 0;
        System.out.println("FILE: " + file.getPath());

        CompilationUnit unit;
        try {
            unit = JavaParser.parse(file);
        } catch (ParseProblemException | FileNotFoundException e) {
            nFailed++;
            e.printStackTrace();
            return nFailed;
        }

        final String dir = String.format("%s/%s", outputDirectory.getPath(), file.getName().replace(".java", ""));
        new File(dir).mkdirs();

        int fileIndex = 0;
        for (var mutator : mutators) {
            final List<String> mutations = mutator.process(unit);

            for (var mutation : mutations) {
                try {
                    var writer = new PrintWriter(String.format("%s/%d.java", dir, fileIndex++), StandardCharsets.UTF_8);
                    writer.println(mutation);
                    writer.close();
                } catch (IOException e) {
                    nFailed++;
                    e.printStackTrace();
                    return nFailed;
                }
            }
        }
        return nFailed;
    }
}