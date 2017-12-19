package searchengine.Ranking;

import searchengine.Indexes.Index;
import searchengine.Website;

/**
 * <pre>
 * This ranker gives all sites the same score; 0.
 * It is used when no ranking is desired
 * </pre>
 */
public class SimpleScore implements Score {

    @Override
    public float getScore(String word, Website website, Index index) {
        return 0;
    }
}
