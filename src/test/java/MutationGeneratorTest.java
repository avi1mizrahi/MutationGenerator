import spoon.Launcher;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.SpoonProgress;

import javax.annotation.processing.AbstractProcessor;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

class MutationGeneratorTest {

    public static final String METHOD_PATH = "src/test/cases/T1.java";
    public static final String TEST_FILE_PATH = "src/test/cases/ColumnFamilyMetricTest.java";

    static String readFile(String path) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded);
    }

    public static void main(String[] args) {

        var l = new Launcher();
        l.addInputResource(METHOD_PATH);

//        var cl = Launcher.parseClass(readFile(METHOD_PATH));
//        System.out.println("!!!!\n" + cl);
//        OneByOneMutator mp = new CommutativeExprMutator();
//        mp.setFactory(cl.getFactory());
//        mp.init();
//
//        for (var o : cl.getElements(new TypeFilter<>(mp.getElementType()))) {
//            System.out.println(o);
//            if (mp.isToBeProcessed((CtElement) o)) {
//                mp.process((CtElement)o);
//            }
//        }
//
//        mp.processingDone();

        var model = l.buildModel();

//        var es = model.getElements(element -> true);
//        System.out.println(es);

//        model.processWith(new LiteralMutator());
//        model.processWith(new RenameMutator());
        model.processWith(new CommutativeExprMutator());
    }
}