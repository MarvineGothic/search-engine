package searchengine.Ranking;

import searchengine.Indexes.Index;
import searchengine.Website;

/**
 * <pre>
 * This interface can rank a website matching a query provided as an input
 * </pre>
 */
public interface Score {

    /**
     * <pre>
     * This method takes a word, a website and an index in order to score the the given website based on the word.
     *
     * @param word    Input String. Depending on the implementation it might allow multiple words and AND and OR statements.
     * @param website Input Website. Takes the website in question.
     * @param index   Input Index. Takes the index we want to use.
     * @return The ranking of the website based on the word.
     * </pre>
     */
    float getScore(String word, Website website, Index index);

}
