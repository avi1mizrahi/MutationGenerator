import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


class MutationGenerator {

    public static void main(String[] args) throws CmdLineException {
        CommandLineParameters commandLineParameters;

        commandLineParameters = new CommandLineParameters(args);

        // TODO: add mutators by command line params
        List<SequentialMutator> mutators = new ArrayList<>();
//        mutators.add(new SequentialMutator(new LiteralMutator()));
        mutators.add(new SequentialMutator(new CommutativeExprMutator()));
        mutators.add(new SequentialMutator(new RenameMutator(name -> new ArrayList<>(List.of("newName1", "b")))));

        final File[] files = Objects.requireNonNull(commandLineParameters.inputDirectory.listFiles(
                pathname -> pathname.isFile() && pathname.getName().toLowerCase().endsWith(".java")));

        int nFailed = 0;

        for (var file : files) {
            System.out.println("FILE: " + file.getPath());

            CompilationUnit unit;
            try {
                unit = JavaParser.parse(file);
            } catch (ParseProblemException | FileNotFoundException e) {
                nFailed++;
                e.printStackTrace();
                continue;
            }

            for (var mutator : mutators) {
                final List<String> mutations = mutator.process(unit);
                if (!mutations.isEmpty()) System.out.println("================================");

                // TODO: write them to file
                mutations.forEach((System.out::println));
            }
        }

        System.out.println("Failed=" + nFailed);
    }
}