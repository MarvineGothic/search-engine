package Main.Indexes;

import Main.Website;

import java.util.ArrayList;
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
    public String word;
    public List<Integer> wordPositions;

    public IndexItem(Website website, String word, List<Integer> wordPositions) {
        this.website = website;
        this.word = word;
        this.wordPositions = wordPositions;
    }

    public IndexItem(Website website, String word) {
        this.website = website;
        this.word = word;
        this.wordPositions = new ArrayList<>();
    }

    public IndexItem(Website website, String word, int wordPosition) {
        this.website = website;
        this.word = word;
        this.wordPositions = new ArrayList<>();
        this.wordPositions.add(wordPosition);
    }

    /**
     * @return The number times the word occurs in the website
     */
    public int getWordCount(){
        return wordPositions.size();
    }

    @Override
    public String toString() {
        return "IndexItem{" +
                "website=" + website.getTitle() +
                ", word='" + word + '\'' +
                ", wordCount=" + getWordCount() +
                '}';
    }
}
