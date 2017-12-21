package searchengine;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * This class contains all the information used by an original website while also containing information on a
 * search word used to find this page. For example, when making a lookup using the InvertedIndex and the word "apple" a
 * list of IndexedWebsites will be returned. In addition to all the information of a normal Website the IndexedWebsite
 * also has pre-calculated the number of times the word "apple" occurs on the site, and also knows the total number of
 * websites containing the word "apple" in the index.
 * This allows to calculate the score of websites much faster when ranking them.
 * </pre>
 */
public class IndexedWebsite extends Website {
    private final Website parent;
    private final String indexWord;
    private int wordFrequency;
    private Integer websitesContainingWordCount = null;

    /**
     * <pre>
     * Creates a copy of the parent website, with additional information based on the given indexWord
     * @param parent The parent website
     * @param indexWord The index word. Must be contained by the parent
     * </pre>
     */
    public IndexedWebsite(@NotNull Website parent, @NotNull String indexWord) {
        if (indexWord == null || parent == null)
            throw new IllegalArgumentException();
        if (!parent.getSetOfWords().contains(indexWord)) {
            throw new IllegalArgumentException("Parent website must contain index word");
        }
        this.parent = parent;
        this.indexWord = indexWord;
        wordFrequency = Collections.frequency(parent.getWords(), indexWord);
    }

    /**
     * <pre>
     * @return Get the number of times the indexWord occurs on the website.
     * </pre>
     */
    public int getWordFrequency() {
        return wordFrequency;
    }

    /**
     * <pre>
     * @return Get the total number of website in the Index that contains the indexWord.
     * </pre>
     */
    public Integer getWebsitesContainingWordCount() {
        return websitesContainingWordCount;
    }

    /**
     * <pre>
     * Set the total number of website in the Index that contains the indexWord.
     * @param websitesContainingWordCount The new value
     * </pre>
     */
    public void setWebsitesContainingWordCount(int websitesContainingWordCount) {
        this.websitesContainingWordCount = websitesContainingWordCount;
    }

    public Website getParent() {
        return parent;
    }

    /**
     * <pre>
     * @return Get the index word supplied in the constructor.
     * </pre>
     */
    public String getIndexWord() {
        return indexWord;
    }

    @Override
    public int compareTo(Website website) {
        return getUrl().compareTo(website.getUrl());
    }

    /**
     * <pre>
     * NOTE: This is the same as the hashcode of the parent website and DOES NOT the the index word into consideration.
     * So the equals method ignores the index word.
     * The reason for this peculiar behavior is that it allows for an elegant sorting algorithm in the
     * QueryHandler.MultiWordQuery method.
     * But it is important to keep in mind that this method works a little different than you might expect
     * @return Get the hashcode of the parent website.
     * </pre>
     */
    @Override
    public int hashCode() {
        return parent.hashCode();
    }

    /**
     * <pre>
     * NOTE: This returns true in two cases:
     * - If other object is a normal website and that website equals the parent.
     * - If the other object is a indexed website and the two parent equals each other.
     * So the equals method ignores the index word.
     * The reason for this peculiar behavior is that it allows for an elegant sorting algorithm in the
     * QueryHandler.MultiWordQuery method.
     * But it is important to keep in mind that this method works a little different than you might expect
     *
     * @param other a object to compare to.
     * @return True if the parent equals the other object.
     * </pre>
     */
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other.getClass() == Website.class)
            return parent.equals(other);
        if (other.getClass() == IndexedWebsite.class) {
            IndexedWebsite indexedWebsite = (IndexedWebsite) other;
            return parent.equals(indexedWebsite.parent);
        }
        return false;
    }

    @Override
    public List<String> getWords() {
        return parent.getWords();
    }

    @Override
    public String getTitle() {
        return parent.getTitle();
    }

    @Override
    public String getUrl() {
        return parent.getUrl();
    }

    @Override
    public Boolean containsWord(String word) {
        return parent.containsWord(word);
    }

    @Override
    public Boolean containsAllWords(List<String> words) {
        return parent.containsAllWords(words);
    }

    @Override
    public String toString() {
        return parent.toString();
    }
}
