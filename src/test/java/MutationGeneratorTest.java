import spoon.Launcher;

class MutationGeneratorTest {
    public static void main(String[] args) {

        var l = new Launcher();
        l.addInputResource("/Users/mizrahi/IdeaProjects/MutationGenerator/src/test/cases/T1.java");

        var model = l.buildModel();

        model.processWith(new LiteralMutator());
        model.processWith(new RenameMutator());
        model.processWith(new CommutativeExprMutator());
    }
}