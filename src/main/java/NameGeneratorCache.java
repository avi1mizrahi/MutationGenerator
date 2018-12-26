import java.util.List;

public class NameGeneratorCache implements NameGenerator {
    private final LruCache<String, List<String>> cache;
    private final NameGenerator                  nameGenerator;

    public NameGeneratorCache(NameGenerator nameGenerator, int cacheSize) {
        this.cache = new LruCache<>(cacheSize);
        this.nameGenerator = nameGenerator;
    }

    @Override
    public List<String> generateNames(String name) {
        return cache.get(name).orElseGet(() -> cache.put(name, nameGenerator.generateNames(name)));
    }
}
