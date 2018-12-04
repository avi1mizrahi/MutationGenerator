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

    public static void main(String[] args) throws CmdLineException, IOException {
        new MutationGenerator(new CommandLineParameters(args));
    }

    MutationGenerator(CommandLineParameters commandLineParameters) throws IOException {
        List<CuMutationProcessor> mutators = new ArrayList<>();

        if (commandLineParameters.flipBinaryExpr) {
            mutators.add(new SequentialMutationProcessor(new BinaryExprMutator()));
        }
        if (commandLineParameters.renameVariable) {
            NameGenerator nameGenerator;
            if (commandLineParameters.word2vecMap != null) {
                nameGenerator = new SimilaritiesFinder(commandLineParameters.word2vecMap, commandLineParameters.numSimilarities);
            } else {
                nameGenerator = new StupidNameGenerator(commandLineParameters.numSimilarities);
            }
            mutators.add(new SequentialMutationProcessor(new RenameMutator(nameGenerator)));
        }

        final File[] files = Objects.requireNonNull(commandLineParameters.inputDirectory.listFiles(
                pathname -> pathname.isFile() && pathname.getName().toLowerCase().endsWith(".java")));

        final ExecutorService threadPool = Executors.newFixedThreadPool(commandLineParameters.numThreads);
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

    private static void processFile(List<CuMutationProcessor> mutators, File file, File outputDirectory) throws IOException {
        System.out.println("FILE: " + file.getPath());

        CompilationUnit unit;
        try {
            unit = JavaParser.parse(file);
        } catch (ParseProblemException | FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        for (var mutator : mutators) {
            final List<String> mutations = mutator.process(unit);

            final String dir = String.format("%s/%s/%s",
                    outputDirectory.getPath(),
                    mutator,
                    file.getName().replace(".java", ""));

            if (!mutations.isEmpty()) {
                if (!new File(dir).mkdirs()) {
                    throw new IOException("Can't create directory: " + dir);
                }
            }

            int fileIndex = 0;
            for (var mutation : mutations) {
                try {
                    var writer = new PrintWriter(String.format("%s/%d.java", dir, fileIndex), StandardCharsets.UTF_8);
                    fileIndex++;
                    writer.println(mutation);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
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
        public Void call() throws IOException {
            processFile(mutators, file, outputDirectory);
            return null;
        }
    }
}