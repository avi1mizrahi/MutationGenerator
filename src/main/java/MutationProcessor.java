import com.github.javaparser.ast.Node;

import java.util.List;

interface MutationProcessor <N extends Node> {
    List<MutatedMethod> process(N compilationUnit);
}
