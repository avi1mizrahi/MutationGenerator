import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

interface CuMutationProcessor {
    List<String> process(CompilationUnit compilationUnit);
}

interface MethodMutationProcessor {
    List<String> process(MethodDeclaration method);
}
