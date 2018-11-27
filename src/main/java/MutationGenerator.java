import org.kohsuke.args4j.CmdLineException;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


class MutationGenerator {

    public static void main(String[] args) throws CmdLineException {
        CommandLineParameters commandLineParameters = null;

        commandLineParameters = new CommandLineParameters(args);

        final File[] files = commandLineParameters.inputDirectory.listFiles(
                pathname -> pathname.isFile() && pathname.getName().toLowerCase().endsWith(".java"));

        int faildToBuilt = 0;

        for (var file : files) {
            System.out.println("FILE: " + file.getPath());
            var launcher = new Launcher();
            launcher.addInputResource(file.getPath());
            launcher.getEnvironment().setNoClasspath(true);
            launcher.getEnvironment().setComplianceLevel(10);

            CtModel model;
            try {
                model = launcher.buildModel();
            } catch (Exception e) {
                faildToBuilt++;
                continue;
            }

            // TODO: add by command line params
            List<OneByOneMutator> mutators = new ArrayList<>();
            mutators.add(new CommutativeExprMutator());

            for (var mutator : mutators) {
                model.processWith(mutator);
                List<CtMethod> methods = mutator.getMutatedMethods();
                // TODO: write them to file
//                for (var method : methods) {
//                    System.out.println(method.getSimpleName());
//                }
            }

        }

        System.out.println("Failed=" + faildToBuilt);
    }
}