package searchengine.Ranking;

import searchengine.Indexes.Index;
import searchengine.Website;

import java.util.List;

/**
 * <pre>
 * Implements a the Score class using the Okapi BM25 algorithm to score the websites.
 * Read https://en.wikipedia.org/wiki/Okapi_BM25 for more information.
 *
 * Formulas and definitions used:
 * dL: words on website = The number of words an the website
 * avDl: Average website words = The average number of words over all websites in the database
 * b: constant parameter = standard value is 0.75
 * k: constant parameter = standard value is 1.75
 * </pre>
 */
public class BM25Score extends TFIDFScore {
    private final float avdL;
    private float totalAmountOfWords;

    /**
     * <pre>
     * Instantiates the current ranker
     * @param websiteList A list of all websites in the index used to perform the queries.
     * </pre>
     */
    public BM25Score(List<Website> websiteList) {
        super(websiteList);
        for (Website site : sites) {
            this.totalAmountOfWords += site.getWords().size();
        }
        avdL = totalAmountOfWords / (sites.size() > 0 ? sites.size() : 1);
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idf(word, index, website) * tfPlus(word, website);
    }

    /**
     * <pre>
     * Calculates the adjusted term frequency compared to the normal tf-idf score algorithm.
     *
     * @param word    The query word
     * @param website The website the query word is found on.
     * @return term frequency
     * </pre>
     */
    private float tfPlus(String word, Website website) {
        float tf = TFScore.tf(word, website);          // term frequency
        float b = 0.75f;
        float k = 1.75f;
        float dL = website.getWords().size();
        return tf * (k + 1) / (k * (1 - b + b * dL / avdL) + tf);
    }
}
