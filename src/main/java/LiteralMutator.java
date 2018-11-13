import spoon.reflect.code.CtLiteral;

public class LiteralMutator extends OneByOneMutator<CtLiteral<Integer>> {
    private Integer originalValue;

    @Override
    public Class getElementType() {
        return CtLiteral.class;
    }

    @Override
    public void doMutation(CtLiteral<Integer> element) {
        originalValue = element.getValue();
        element.setValue(55);
    }

    @Override
    public void undoMutation(CtLiteral<Integer> element) {
        element.setValue(originalValue);
    }
}
