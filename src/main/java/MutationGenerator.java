import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MutationGenerator {

    public static void main(String[] args) throws
            CmdLineException,
            IOException,
            InterruptedException {
        new MutationGenerator(new CommandLineParameters(args));
    }

    MutationGenerator(CommandLineParameters parameters) throws IOException, InterruptedException {
        List<MutationProcessor<CompilationUnit>> mutators = new ArrayList<>();

        if (parameters.flipBinaryExpr) {
            mutators.add(new SequentialMutationProcessor(new BinaryExprMutator(),
                                                         parameters.maxMutationsPerMethod));
        }
        if (parameters.renameVariable) {
            NameGenerator nameGenerator;
            if (parameters.word2vecMap != null) {
                nameGenerator = new SimilaritiesFinder(parameters.word2vecMap,
                                                       parameters.numSimilarities);
            } else {
                nameGenerator = new StupidNameGenerator(parameters.numSimilarities);
            }
            if (parameters.renameCacheSize > 0) {
                nameGenerator = new NameGeneratorCache(nameGenerator, parameters.renameCacheSize);
            }
            mutators.add(new SequentialMutationProcessor(new RenameMutator(nameGenerator),
                                                         parameters.maxMutationsPerMethod));
        }

        final ExecutorService threadPool = Executors.newFixedThreadPool(parameters.numThreads);
        final Path inputDir = parameters.inputDirectory.toPath();
        Files.find(inputDir,
                   4,
                   (path, basicFileAttributes) ->
                           basicFileAttributes.isRegularFile() &&
                                   path.toString().endsWith(".java"))
             .forEach(path -> threadPool.submit(new FileProcessTask(mutators,
                                                                    path.toFile(),
                                                                    parameters.inputDirectory.toPath(),
                                                                    parameters.outputDirectory)));
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);
    }

    private static void processFile(List<MutationProcessor<CompilationUnit>> mutators,
                                    File file,
                                    Path inputDirectory,
                                    File outputDirectory) throws IOException {
        System.out.println("Parsing file: " + file.getPath());

        CompilationUnit unit;
        try {
            unit = JavaParser.parse(file);
        } catch (ParseProblemException | FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        var relativeDirPathOfFile = inputDirectory.relativize(file.toPath())
                                                  .toString()
                                                  .replace(".java", "");
        for (var mutator : mutators) {
            final List<MutatedMethod> mutations = mutator.process(unit);

            final String dir = String.format("%s/%s/%s",
                                             outputDirectory.getPath(),
                                             mutator,
                                             relativeDirPathOfFile);

            if (!mutations.isEmpty()) {
                if (!new File(dir).mkdirs()) {
                    throw new IOException("Can't create directory: " + dir);
                }
            }

            int fileIndex = 0;
            for (var mutation : mutations) {
                try {
                    var writer = new PrintWriter(String.format("%s/%d.java", dir, fileIndex),
                                                 StandardCharsets.UTF_8);
                    fileIndex++;
                    writer.println(mutation.getCode());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private static class FileProcessTask implements Callable<Void> {
        private final List<MutationProcessor<CompilationUnit>> mutators;
        private final File file;
        private final File outputDirectory;
        private final Path inputDirectory;

        FileProcessTask(List<MutationProcessor<CompilationUnit>> mutators,
                        File file,
                        Path inputDirectory,
                        File outputDirectory) {
            this.mutators = mutators;
            this.file = file;
            this.outputDirectory = outputDirectory;
            this.inputDirectory = inputDirectory;
        }

        @Override
        public Void call() throws IOException {
            processFile(mutators, file, inputDirectory, outputDirectory);
            return null;
        }
    }
}