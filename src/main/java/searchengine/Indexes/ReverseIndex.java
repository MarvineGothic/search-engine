package searchengine.Indexes;

import org.apache.catalina.util.ResourceSet;
import searchengine.IndexedWebsite;
import searchengine.Website;

import java.util.*;


/**
 * Implements the Index interface using a reverse index method
 */
abstract public class ReverseIndex implements Index {
    protected Map<String, HashSet<IndexedWebsite>> wordMap;

    // TODO: 12/13/2017 This method can probably be removed
//    /**
//     * This method returns information about all websites matching the query.
//     * It contain information about where the words occurs in the website and how many times.
//     * @param query A single word. The word should be valid according to the "validateQuery" method.
//     * @return All websites matching the query in the form of IndexItems
//     */
//    public HashSet<IndexedWebsite> lookupIndexItems(String query){
//        return wordMap.getOrDefault(query, new HashSet<>());
//    }
    protected abstract void InitializeWordMap();

    public void build(List<Website> websiteList) {
        InitializeWordMap();
        for (Website currentSite : websiteList){
            for (String wordOnSite : currentSite.getWords()){
//                The statement in this for loop is equivalent to the two lines below, but should be faster.
//                wordMap.computeIfAbsent(wordOnSite, key -> new HashSet<>());
//                wordMap.get(wordOnSite).add(new IndexItem(currentSite, wordOnSite));
                wordMap.compute(wordOnSite, (key, oldValue) -> {
                    if (oldValue == null) {
                        oldValue = new HashSet<>();
                    }
                    oldValue.add(new IndexedWebsite(currentSite, wordOnSite));
                    return oldValue;
                });
            }
        }
    }

    @Override
    public List<Website> lookup(String queryWord) {
        if (wordMap== null)
            return null;
        List<Website> sites = new ArrayList<>();
        sites.addAll(wordMap.getOrDefault(queryWord, new HashSet<>()));
        return new ArrayList<>(sites);
    }

    /**
     * Method used for test purposes to compare expected and actual wordMap results
     */
    @Override
    public String toString() {
        return "Mapped values=" + wordMap;
    }

    public Map<String, HashSet<Website>> getWordMap() {
        if (wordMap == null)
            return null;
        Map<String, HashSet<Website>> map = new HashMap<>();
        for (Map.Entry<String, HashSet<IndexedWebsite>> entry : wordMap.entrySet()){
            HashSet<Website> siteSet = new HashSet<>();
            siteSet.addAll(entry.getValue());
            map.put(entry.getKey(), siteSet);
        }
        return map;
    }
}
