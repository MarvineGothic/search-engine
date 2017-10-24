package Main.Indexes;

import Main.FileHelper;
import Main.Website;

import java.io.File;
import java.util.*;


/**
 * Implements the Index interface using a reverse index method
 */
abstract public class ReverseIndex implements Index {
    protected Map<String, HashSet<Website>> wordMap;

    /**
     * Test to see if the method works
     * @param args (no args needed)
     */
    public static void main(String args[]){
        String dir = System.getProperty("user.dir");
        List<Website> sites = FileHelper.parseFile(dir + File.separator + "data" + File.separator + "enwiki-small.txt");

        ReverseIndex index = new ReverseHashMapIndex();
        index.build(sites);
        System.out.println(index.lookup("modern").size());
        List<IndexItem> result = index.lookupIndexItems("district");
        for (IndexItem item : result){
            System.out.println(item);
        }
    }

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

    abstract void InitializeWordMap();

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
    public List<Website> lookup(String query) {
        HashSet<Website> sites = wordMap.getOrDefault(query, new HashSet<>());
        return new ArrayList<>(sites);
    }

    /**
     * A valid query is a string that only contains numeric symbols 0-9 or letters a-z (both small and large letters).
     * @param query The query to test
     * @return True if query is valid
     */
    @Override
    public Boolean validateQuery(String query) {
        // Checks if the query contains any non standard letters and numbers
        String strippedWord = query.replaceAll("[^a-zA-Z0-9]", "");
        return strippedWord.length() >= query.length();
       // return query.replaceAll("[^a-zA-Z0-9]", "").length() >= query.length();    // just one line ;) Serg
    }
}
