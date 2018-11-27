import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

abstract class OneByOneMutator<E extends CtElement> extends AbstractProcessor<E> {
    private final ArrayList<CtMethod> mutations = new ArrayList<>();

    protected abstract void doMutation(E element);

    protected abstract void undoMutation(E element);

    protected abstract Class getElementType();

    public List<CtMethod> getMutatedMethods() {
        return mutations;
    }

    @Override
    public void init() {
        super.init();

        addProcessedElementType(getElementType());
        System.out.println("MUTATOR:" + this.getClass().getSimpleName());
    }

    @Override
    public boolean isToBeProcessed(E candidate) {
        return super.isToBeProcessed(candidate) && candidate.getParent(CtMethod.class) != null;
    }

    @Override
    public void process(E element) {
        doMutation(element);

        var mutant = element.getFactory().Core().clone(element.getParent(CtMethod.class));
        mutations.add(mutant);

        undoMutation(element);
    }

    @Override
    public void processingDone() {
//        for (var mutant : mutations) {
//            System.out.println(mutant);
//        }
        System.out.println("DONE!\n");

        super.processingDone();
    }
}
