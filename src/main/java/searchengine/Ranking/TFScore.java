package searchengine.Ranking;

import searchengine.IndexedWebsite;
import searchengine.Indexes.Index;
import searchengine.Website;

import java.util.Collections;

/**
 * <pre>
 * Implements the Score interface and adds the method tf which calculates the term frequency of a given word.
 * </pre>
 */
public class TFScore implements Score {

    /**
     * <pre>
     * Calculates the term frequency as the number of times a word occurs on a website
     * @param word    The query word
     * @param website The website the query word is found on.
     * @return term frequency
     * </pre>
     */
    public static float tf(String word, Website website) {
        try {
            return ((IndexedWebsite) website).getWordFrequency();
        } catch (ClassCastException | NullPointerException e) {
            return Collections.frequency(website.getWords(), word);
        }
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return tf(word, website);
    }
}


