package searchengine.Indexes;

import searchengine.IndexedWebsite;
import searchengine.Website;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Implements the Index interface using a reverse index method
 * </pre>
 */
abstract public class InvertedIndex implements Index {
    Map<String, HashSet<IndexedWebsite>> wordMap;

    /**
     * <pre>
     * This method assigns the wordMap, leaving the choice of using either a HashMap, TreeMap or any other map to
     * the implementation class.
     * </pre>
     */
    protected abstract void InitializeWordMap();

    @Override
    public void build(List<Website> websiteList) {
        InitializeWordMap();
        for (Website currentSite : websiteList) {
            for (String indexWord : currentSite.getSetOfWords()) {
                wordMap.compute(indexWord, (key, value) -> {
                    if (value == null) {
                        value = new HashSet<>();
                    }
                    IndexedWebsite site = new IndexedWebsite(currentSite, indexWord);
                    value.add(site);
                    return value;
                });
            }
        }
        assignWebsitesContainingWordCount();
    }

    /**
     * <pre>
     * For each word, this method assigns all the the number of websites containing that word to each IndexedWebsites
     * </pre>
     */
    private void assignWebsitesContainingWordCount() {
        for (Map.Entry<String, HashSet<IndexedWebsite>> entry : wordMap.entrySet()) {
            int count = entry.getValue().size();
            for (IndexedWebsite website : entry.getValue()) {
                website.setWebsitesContainingWordCount(count);
            }
        }
    }

    @Override
    public List<Website> lookup(String queryWord) {
        return new ArrayList<>(wordMap.getOrDefault(queryWord, new HashSet<>()));
    }

    /**
     * <pre>
     * Method used for test purposes to compare expected and actual wordMap results
     * </pre>
     */
    @Override
    public String toString() {
        return "Mapped values=" + wordMap;
    }


    /**
     * <pre>
     * @return the mapping of queryWords to websites.
     * </pre>
     */
    public Map<String, HashSet<IndexedWebsite>> getWordMap() {
        return wordMap;
    }
}
