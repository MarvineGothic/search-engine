package searchengine.Ranking;

import searchengine.IndexedWebsite;
import searchengine.Indexes.Index;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * Implements a the IRanker class using the tf-idf algorithm to score the websites.
 * Read https://en.wikipedia.org/wiki/Tf-idf for more information.
 *
 * Formulas and definitions used:
 * tf: Term frequency = Number of times a word occurs on a website
 * d: Database = The total number of websites in the database
 * n: Websites occurrence = The number of websites containing a specific word
 * idf: Inverse Document Frequency = log2(d/n)
 * </pre>
 */
public class RankerIDF implements IRanker {
    protected List<Website> sites = new ArrayList<>();

    /**
     * Instantiates the current ranker
     *
     * @param websiteList A list of all websites in the index used to perform the queries.
     *                    </pre>
     */
    public RankerIDF(List<Website> websiteList) {
        sites = websiteList;
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idf(word, index, website) * tf(word, website);
    }

    /**
     * Calculates the term frequency as the number of times a word occurs on a website
     *
     * @param word    The query word
     * @param website The website the query word is found on.
     * @return term frequency
     * </pre>
     */
    public float tf(String word, Website website) {
        try {
            return ((IndexedWebsite) website).getWordFrequency();
        } catch (ClassCastException e) {
            return Collections.frequency(website.getWords(), word);
        }
    }

    /**
     * Calculates the Inverse Document Frequency for the given website and query word
     *
     * @param word    The query word
     * @param index   The index where the query is performed
     * @param website The website the query word is found on.
     * @return The Inverse Document Frequency
     * </pre>
     */
    public float idf(String word, Index index, Website website) {
        if (sites.size() == 0)
            return -1;
        float d = sites.size();
        float n;
        try {
            n = ((IndexedWebsite) website).getWebsitesContainingWordCount();
        } catch (ClassCastException e) {
            n = index.lookup(word).size();
        }
        if (n <= 0.00000001)
            return -1;
        return (float) (Math.log(d / n) / Math.log(2));
    }
}


