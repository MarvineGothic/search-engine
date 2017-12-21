package searchengine.Ranking;

import searchengine.IndexedWebsite;
import searchengine.Indexes.Index;
import searchengine.Website;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Implements the Score class and adds the method idf
 * which calculates the inverse document frequency algorithm to score the websites.
 *
 * Formulas and definitions used:
 * d: Database = The total number of websites in the database
 * n: Websites occurrence = The number of websites containing a specific word
 * idf: Inverse Document Frequency = log2(d/n)
 * </pre>
 */
public class IDFScore implements Score {
    List<Website> sites = new ArrayList<>();

    /**
     * <pre>
     * Instantiates the current ranker
     * @param websiteList A list of all websites in the index used to perform the queries.
     * </pre>
     */
    public IDFScore(List<Website> websiteList) {
        sites = websiteList;
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idf(word, index, website);
    }

    /**
     * <pre>
     * Calculates the Inverse Document Frequency for the given website and query word
     * @param word    The query word
     * @param index   The index where the query is performed
     * @param website The website the query word is found on.
     * @return The Inverse Document Frequency: log2(d/n).
     * </pre>
     */
    float idf(String word, Index index, Website website) {
        if (sites.size() == 0)
            return -1;
        float d = sites.size();
        float n;
        try {
            n = ((IndexedWebsite) website).getWebsitesContainingWordCount();

        } catch (ClassCastException e) {
            n = index.lookup(word).size();
        }
        if (n <= 1e-6 || d <= 1e-6)
            return -1;
        return (float) (Math.log(d / n) / Math.log(2));
    }
}


