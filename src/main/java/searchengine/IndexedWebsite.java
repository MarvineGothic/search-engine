package searchengine;

import javax.validation.constraints.NotNull;
import java.util.Collections;

public class IndexedWebsite extends Website {
    private final Website parent;
    private final String indexWord;
    private int wordFrequency;

    public IndexedWebsite(Website parent, @NotNull String indexWord) {
        super(parent.getUrl(), parent.getTitle(), parent.getWords());
        if (indexWord == null)
            throw new IllegalArgumentException("indexWord cannot be null");
        this.parent = parent;
        this.indexWord = indexWord;
        wordFrequency = Collections.frequency(parent.getWords(), indexWord);
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
        return super.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other.getClass() == Website.class)
            return parent.equals(other);
        if (other.getClass() == IndexedWebsite.class){
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
