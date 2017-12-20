package searchengine.Ranking;

import searchengine.IndexedWebsite;
import searchengine.Indexes.Index;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * Implements the Score class using the tf and idf algorithms to score the websites.
 * Read https://en.wikipedia.org/wiki/Tf-idf for more information.
 *
 * Formulas and definitions used:
 * tf: Term frequency = Number of times a word occurs on a website
 * d: Database = The total number of websites in the database
 * n: Websites occurrence = The number of websites containing a specific word
 * idf: Inverse Document Frequency = log2(d/n)
 * </pre>
 */
public class TFIDFScore extends IDFScore {
    /**
     * <pre>
     * Instantiates the current ranker
     * @param websiteList A list of all websites in the index used to perform the queries
     * </pre>
     */
    public TFIDFScore(List<Website> websiteList) {
        super(websiteList);
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idf(word, index, website) * TFScore.tf(word, website);
    }
}


