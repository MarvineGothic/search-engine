package searchengine.Ranking;

import searchengine.IndexedWebsite;
import searchengine.Indexes.Index;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * Implements a the Score class using simple term frequency.
 */
public class TFScore implements Score {

    @Override
    public float getScore(String word, Website website, Index index) {
        return tf(word, website);
    }

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
        } catch (ClassCastException e) {
            return Collections.frequency(website.getWords(), word);
        }
    }
}


