import java.util.List;


/**
 * Author Rasmus F
 * Use this IndexItem as the item stored with reverse index.
 * For example, when looking up the word "apple" the reverse index should return a array of Index items.
 * Each index item is a reference to a website, but also contains
 * - At what index the word occurs in the website.
 * - The number times the word occur
 */
public class IndexItem {
    public Website website;
    public String indexWord;
    public List<Integer> wordIndexes;

    public IndexItem(Website website, String indexWord, List<Integer> wordIndexes) {
        this.website = website;
        this.indexWord = indexWord;
        this.wordIndexes = wordIndexes;
    }

    /**
     * @return The number times the word occurs in the website
     */
    public int getWordCount(){
        return wordIndexes.size();
    }
}
