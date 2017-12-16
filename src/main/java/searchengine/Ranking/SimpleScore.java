package searchengine.Ranking;

import searchengine.Indexes.Index;
import searchengine.Website;

/**
 * <pre>
 * This ranker gives all site the same score (Used when no ranking is desired)
 * </pre>
 */
public class SimpleScore implements Score {

    @Override
    public float getScore(String word, Website website, Index index) {
        return 0;
    }
}
