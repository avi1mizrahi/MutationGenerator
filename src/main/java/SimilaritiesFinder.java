import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimilaritiesFinder implements NameGenerator {
    private final Searcher similaritySearcher;
    private final int n;

    /**
     * @param savedModelFile - path to word2vec text-file model
     * @param n - number of similarities for {@code generateNames} to return
     * @throws IOException
     */
    SimilaritiesFinder(File savedModelFile, int n) throws IOException {
        similaritySearcher = loadModel(savedModelFile);
        this.n = n;
    }

    private static Searcher loadModel(File savedModelFile) throws IOException {
        return Word2VecModel.fromTextFile(savedModelFile).forSearch();
    }

    @Override
    public List<String> generateNames(String name) {
        List<String> matches;
        try {
            matches = similaritySearcher.getMatches(name, n).stream().map(Searcher.Match::match).collect(Collectors.toList());
        } catch (Searcher.UnknownWordException e) {
            matches = Collections.emptyList();
        }

        return matches;
    }
}
