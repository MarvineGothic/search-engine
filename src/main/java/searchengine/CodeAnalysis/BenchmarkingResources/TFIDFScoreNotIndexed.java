package searchengine.CodeAnalysis.BenchmarkingResources;

import searchengine.Indexes.Index;
import searchengine.Ranking.Score;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     NOTE: This class is identical to the TFIDFScore class except it does not use IndexedWebsites and is
 *     only used for performance.
 * Implements a the Score class using the tf-idf algorithm to score the websites.
 * Read https://en.wikipedia.org/wiki/Tf-idf for more information.
 *
 * Formulas and definitions used:
 * tf: Term frequency = Number of times a word occurs on a website
 * d: Database = The total number of websites in the database
 * n: Websites occurrence = The number of websites containing a specific word
 * idf: Inverse Document Frequency = log2(d/n)
 * </pre>
 */
@Deprecated
public class TFIDFScoreNotIndexed implements Score {
    protected List<Website> sites = new ArrayList<>();

    /**
     * <pre>
     * Instantiates the current ranker
     * @param websiteList A list of all websites in the index used to perform the queries.
     *                    </pre>
     */
    public TFIDFScoreNotIndexed(List<Website> websiteList) {
        sites = websiteList;
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idf(word, index) * tf(word, website);
    }

    /**
     * <pre>
     * Calculates the term frequency as the number of times a word occurs on a website
     * @param word    The query word
     * @param website The website the query word is found on.
     * @return term frequency
     * </pre>
     */
    public float tf(String word, Website website) {
        return Collections.frequency(website.getWords(), word);
    }

    /**
     * <pre>
     * Calculates the Inverse Document Frequency for the given website and query word
     * @param word    The query word
     * @param index   The index where the query is performed
     * @return The Inverse Document Frequency
     * </pre>
     */
    public float idf(String word, Index index) {
        if (sites.size() == 0)
            return -1;
        float d = sites.size();
        float n = index.lookup(word).size();
        if (n <= 0.00000001)
            return -1;
        return (float) (Math.log(d / n) / Math.log(2));
    }
}


