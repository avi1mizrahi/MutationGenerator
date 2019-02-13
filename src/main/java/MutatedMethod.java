import com.github.javaparser.ast.body.MethodDeclaration;

class MutatedMethod {
    private final String code;

    MutatedMethod(String code) {
        this.code = code;
    }

    static MutatedMethod from(MethodDeclaration methodDeclaration) {
        return new MutatedMethod(methodDeclaration.toString());
    }

    public String getCode() {
        return code;
    }
}
