package searchengine;

import javax.validation.constraints.NotNull;

public class IndexedWebsite extends Website {
    private final Website parent;
    private final String indexWord;

    public IndexedWebsite(Website parent, @NotNull String indexWord) {
        super(parent.getUrl(), parent.getTitle(), parent.getWords());
        if (indexWord == null)
            throw new IllegalArgumentException("indexWord cannot be null");
        this.parent = parent;
        this.indexWord = indexWord;
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

        if (this == other) return true;
        if (other == null)
            return false;
        if ( getClass().equals(Website.class)) {
            Website website = (Website) other;
            return parent.equals(website);
        }
        if (getClass() == other.getClass()){
            IndexedWebsite indexedWebsite = (IndexedWebsite) other;
            return parent.equals(indexedWebsite.parent) && indexWord.equals(indexedWebsite.indexWord);
        }
        return false;
    }
}
