import com.github.javaparser.ast.body.MethodDeclaration;

class MutatedMethod {
    private final String methodName;
    private final String code;
    private       int    index;

    private MutatedMethod(String methodName, String code, int index) {
        this.code = code;
        this.methodName = methodName;
        this.index = index;
    }

    static MutatedMethod from(MethodDeclaration methodDeclaration, int index) {
        return new MutatedMethod(methodDeclaration.getNameAsString(),
                                 methodDeclaration.toString(),
                                 index);
    }

    static MutatedMethod from(String methodName, String code, int index) {
        return new MutatedMethod(methodName, code, index);
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
}