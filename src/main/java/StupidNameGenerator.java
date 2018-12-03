import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StupidNameGenerator implements NameGenerator {
    private final int n;

    StupidNameGenerator(int n) {
        this.n = n;
    }

    @Override
    public List<String> generateNames(String name) {
        return IntStream.range(0, n).mapToObj(i -> name + i).collect(Collectors.toList());
    }
}
