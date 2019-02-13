import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;

class MutatedMethod {
    public static final Range    DEFAULT_RANGE = Range.range(0,
                                                             0,
                                                             0,
                                                             0);
    private final       String   methodName;
    private final       String   code;
    private final       Position position;
    private final       int      index;

    MutatedMethod(String methodName, String code, Position position, int index) {
        this.code = code;
        this.methodName = methodName;
        this.position = position;
        this.index = index;
    }

    static MutatedMethod from(MethodDeclaration methodDeclaration, int index) {
        return new MutatedMethod(methodDeclaration.getNameAsString(),
                                 methodDeclaration.toString(),
                                 methodDeclaration.getRange()
                                                  .orElse(DEFAULT_RANGE).begin,
                                 index);
    }

    static MutatedMethod from(String methodName, String code) {
        return new MutatedMethod(methodName, code, DEFAULT_RANGE.begin, 0);
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

    public Position getPosition() {
        return position;
    }
}