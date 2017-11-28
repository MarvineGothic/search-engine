package searchengine.Indexes;

import searchengine.Website;

import java.util.*;


/**
 * Implements the Index interface using a reverse index method
 */
abstract public class ReverseIndex implements Index {
    protected Map<String, HashSet<Website>> wordMap;

    /**
     * This method returns information about all websites matching the query.
     * It contain information about where the words occurs in the website and how many times.
     * @param query A single word. The word should be valid according to the "validateQuery" method.
     * @return All websites matching the query in the form of IndexItems
     */
    public List<IndexItem> lookupIndexItems(String query){
        List<IndexItem> itemList = new ArrayList<>();
        for (Website site : lookup(query)){
            itemList.add(new IndexItem(site, query, site.getWordPositions(query)));
        }
        return itemList;
    }

    protected abstract void InitializeWordMap();

    @Override
    public void build(List<Website> websiteList) {
        InitializeWordMap();
        for (Website currentSite : websiteList){
            for (String wordOnSite : currentSite.getWords()){
//                The statement in this for loop is equivalent to the two lines below, but should be faster.
//                wordMap.computeIfAbsent(wordOnSite, key -> new HashSet<>());
//                wordMap.get(wordOnSite).add(currentSite);
                wordMap.compute(wordOnSite, (key, oldValue) -> {
                    if (oldValue == null) {
                        oldValue = new HashSet<>();
                    }
                    oldValue.add(currentSite);
                    return oldValue;
                });
            }
        }
    }

    @Override
    public List<Website> lookup(String queryWord) {
        HashSet<Website> sites = wordMap.getOrDefault(queryWord, new HashSet<>());
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
        return wordMap;
    }
}
