package searchengine.Indexes;

import searchengine.Website;

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
    public final Website website;
    public final String word;
    public final List<Integer> wordPositions;

    public IndexItem(Website website, String word) {
        this.website = website;
        this.word = word;
        this.wordPositions = website.getWordPositions(word);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexItem indexItem = (IndexItem) o;

        if (!website.equals(indexItem.website)) return false;
        return word.equals(indexItem.word);
    }

    @Override
    public int hashCode() {
        int result = website.hashCode();
        result = 31 * result + word.hashCode();
        return result;
    }

    /**
     * @return The number times the word occurs in the website
     */
    public int getWordCount() {
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
