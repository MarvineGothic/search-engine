package searchengine;

import java.util.HashSet;
import java.util.List;

/**
 * <pre>
 * The Website class is the base element of the Search Engine, and is used for storing information in the database
 * as well as searching the database.
 * </pre>
 */
public class Website implements Comparable<Website> {
    private String title;
    private String url;
    private List<String> words;
    private HashSet<String> setOfWords;

    /**
     * <pre>
     * Upon construction of a new Website element, the modifyWordList method of QueryHandler is called on the list
     * of keywords given to the constructor. This removes unnecessary punctuation and spaces, and converts the keywords
     * to lower case, to remove case sensitivity in searches.
     *
     * To allow finding keywords in a Website without having to iterate through elements of a List, the words are
     * deposited into a HashSet.
     *
     * @param url   gives the location of the Website element
     * @param title is the name of the Website element
     * @param words is a list of the most commonly used keywords on a given website
     * </pre>
     */
    public Website(String url, String title, List<String> words) {
        if (url == null || title == null || words == null)
            throw new IllegalArgumentException();
        this.url = url;
        this.title = title;
        this.words = QueryHandler.modifyWordList(words);
        setOfWords = new HashSet<>(this.words);
    }

    /**
     * <pre>
     * This constructor is used for the IndexedWebsite subclass.
     * </pre>
     */
    Website() {
    }

    public List<String> getWords() {
        return words;
    }

    public String getTitle() {
        return title;
    }

    public HashSet<String> getSetOfWords() {
        return setOfWords;
    }

    public String getUrl() {
        return url;
    }

    /**
     * <pre>
     * The method checks whether the Website's HashSet of keywords contains a given single query word
     *
     * @param word is a query word given by the user
     * @return returns true if the HashSet containing the Website's keywords contains the query word, and false if
     * if it doesn't
     * </pre>
     */
    public Boolean containsWord(String word) {
        return setOfWords.contains(word);
    }

    /**
     * <pre>
     * The method checks whether the Website's HashSet of keywords contains a list given single query words
     *
     * @param words is a list of query words given by the user
     * @return returns true if the HashSet containing the Website's keywords contains the query word, and false if
     * if it doesn't
     * </pre>
     */
    public Boolean containsAllWords(List<String> words) {
        return setOfWords.containsAll(words);
    }

    /**
     * <pre>
     * The method compares a given Website with another by comparing the websites' URL, used for sorting URLs
     * alphabetically. This is used for testing purposes.
     *
     * @param website a Website element to be compared with this Website
     * @return returns 0 if the elements are lexicographically equal, less than 0 if the argument is lexicographically
     * greater than this Website and greater than 0 if the argument is lexicographically lesser.
     * </pre>
     */
    @Override
    public int compareTo(Website website) {
        return url.compareTo(website.url);
    }

    @Override
    public String toString() {
        String output = "Title: " + title + "\n";
        output += "url: " + url + "\n";
        output += "words: " + String.join("; ", words) + "\n";
        return output;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null)
            return false;
        if (IndexedWebsite.class.equals(other.getClass())) {
            return equals(((IndexedWebsite) other).getParent());
        }
        if (Website.class == other.getClass()) {
            Website website = (Website) other;
            return title.equals(website.title) && url.equals(website.url) && words.equals(website.words);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + words.hashCode();
        return result;
    }


}
