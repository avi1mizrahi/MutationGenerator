import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;

class MutatedMethod {
    private static final Range DEFAULT_RANGE = Range.range(0,
                                                           0,
                                                           0,
                                                           0);

    private final String   methodName;
    private final String   code;
    private final Position position;

    private int index = 0;

    MutatedMethod(String methodName, String code, Position position) {
        this.code = code;
        this.methodName = methodName;
        this.position = position;
    }

    static MutatedMethod from(MethodDeclaration methodDeclaration) {
        return new MutatedMethod(methodDeclaration.getNameAsString(),
                                 methodDeclaration.toString(),
                                 methodDeclaration.getRange()
                                                  .orElse(DEFAULT_RANGE).begin);
    }

    static MutatedMethod from(String methodName, String code) {
        return new MutatedMethod(methodName, code, DEFAULT_RANGE.begin);
    }

    public String getCode() {
        return code;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Position getPosition() {
        return position;
    }
}