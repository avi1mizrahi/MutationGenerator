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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        final ExecutorService threadPool = Executors.newFixedThreadPool(8);
        final List<Callable<Void>> tasks = new ArrayList<>();

        for (var file : files) {
            tasks.add(new FileProcessTask(mutators, file, commandLineParameters.outputDirectory));
        }

        try {
            threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
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

    private static class FileProcessTask implements Callable<Void> {
        private final List<CuMutationProcessor> mutators;
        private final File file;
        private final File outputDirectory;

        FileProcessTask(List<CuMutationProcessor> mutators, File file, File outputDirectory) {
            this.mutators = mutators;
            this.file = file;
            this.outputDirectory = outputDirectory;
        }

        @Override
        public Void call() {
            processFile(mutators, file, outputDirectory);
            return null;
        }
    }
}