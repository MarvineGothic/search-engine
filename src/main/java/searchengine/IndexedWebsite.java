package searchengine;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class IndexedWebsite extends Website {
    private final Website parent;
    private final String indexWord;
    private int wordFrequency;
    private int websitesContainingWordCount;

    public IndexedWebsite(@NotNull Website parent, @NotNull String indexWord) {
//        super(parent.getUrl(), parent.getTitle(), parent.getWords());
        if (indexWord == null || parent == null)
            throw new IllegalArgumentException();
        this.parent = parent;
        this.indexWord = indexWord;
        wordFrequency = Collections.frequency(parent.getWords(), indexWord);
    }

    public int getWebsitesContainingWordCount() {
        return websitesContainingWordCount;
    }

    public void setWebsitesContainingWordCount(int websitesContainingWordCount) {
        this.websitesContainingWordCount = websitesContainingWordCount;
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

    @Override
    public List<Integer> getWordPositions(String word) {
        return parent.getWordPositions(word);
    }

    @Override
    public int compareTo(Website website) {
        if (website.getClass() == IndexedWebsite.class)
            return parent.compareTo(((IndexedWebsite) website).parent);
        return parent.compareTo(website);
    }

    public int getWordFrequency() {
        return wordFrequency;
    }

    public Website getParent() {
        return parent;
    }

    public String getIndexWord() {
        return indexWord;
    }

    @Override
    public int hashCode() {
        return parent.hashCode();
    }

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

//
//        if (this == other) return true;
//        if (other == null)
//            return false;
//        if (Website.class == other.getClass()) {
//            Website website = (Website) other;
//            return parent.equals(website);
//        }
//        if (getClass() == other.getClass()){
//            IndexedWebsite indexedWebsite = (IndexedWebsite) other;
////            return parent.equals(indexedWebsite.parent) && indexWord.equals(indexedWebsite.indexWord);
//            return parent.equals(indexedWebsite.parent) && indexWord.equals(indexedWebsite.indexWord);
//        }
//        return false;
    }
}
