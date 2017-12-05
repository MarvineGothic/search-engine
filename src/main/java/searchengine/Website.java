package searchengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The Website class is the base element of the Search Engine, and is used for storing information in the database
 * as well as searching the database.
 */
public class Website implements Comparable<Website> {
    private String title;
    private String url;
    private List<String> words;
    private HashSet<String> setOfWords;

    /**
     * Upon construction of a new Website element, the modifyWordlist method of IndexMethods is called on the list
     * of keywords given to the constructor. This removes unnecessary punctuation and spaces, and converts the keywords
     * to lower case, to remove case sensitivity in searches.
     *
     * To allow finding keywords in a Website without having to iterate through elements of a List, the words are
     * deposited into a HashSet.
     * @param url gives the location of the Website element
     * @param title is the name of the Website element
     * @param words is a list of the most commonly used keywords on a given website
     */
    public Website(String url, String title, List<String> words) {
        this.url = url;
        this.title = title;
        this.words = IndexMethods.modifyWordList(words);
        setOfWords = new HashSet<>(this.words);
    }

    public List<String> getWords() {
        return words;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    /**
     *
     * @param word
     * @return
     */
    public Boolean containsWord(String word) {
        return setOfWords.contains(word);
    }

    public Boolean containsAllWords(List<String> words) {
        return setOfWords.containsAll(words);
    }

    @Override
    public String toString() {
        String output = "Title: " + title + "\n";
        output += "url: " + url + "\n";
        output += "words: " + String.join("; ", words) + "\n";
        return output;
    }

    /**
     * Get a list of where the word occurs on the website.
     *
     * @param word The word to check for.
     * @return A list of positions, where the positions is defined as the number of other words that occurs on the
     * website before the given word.
     */
    public List<Integer> getWordPositions(String word) {
        List<Integer> wordPositions = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals(word)) {
                wordPositions.add(i);
            }
        }
        return wordPositions;
    }

    /**
     * The method compares a given Website with another by comparing the websites' URL.
     * @param website a Website element to be compared with this Website
     * @return returns true if the URLs are the same and false if they are not
     */
    @Override
    public int compareTo(Website website) {
        return url.compareTo(website.url);
    }
}
