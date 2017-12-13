package searchengine.Indexes;

import searchengine.Website;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class SimpleIndex implements Index {

    public static void main(String[] args) {}

    private List<Website> sites;

    public SimpleIndex() {
        this.sites = new ArrayList<>();
    }


    /**
     * Returns a list of the indexed websites
     */


    /**
     *
     * @param websiteList The full list of websites that should be processed
     *                    it "builds" a List of websites from given parameter
     *                    then removes repeated words and sort in alphabetic order
     */
    @Override
    public void build(List<Website> websiteList) {
        this.sites = websiteList;
    }

    @Override
    public List<Website> lookup(String queryWord) {
        List<Website> newList = new ArrayList<>();
        // Go through all websites and check if query is present
        for (Website website : sites) {
            // If query is present, add it to list of websites newList
            if (website.containsWord(queryWord)) {
                newList.add(website);
            }
        }
        return newList;
    }

//    @Override
//    public HashSet<IndexItem> lookupIndexItems(String queryWord) {
//        HashSet<IndexItem> indexItems = new HashSet<>();
//        // Go through all websites and check if query is present
//        for (Website website : sites) {
//            // If query is present, add it to list of websites newList
//            if (website.containsWord(queryWord)) {
//                indexItems.add(new IndexItem(website, queryWord));
//            }
//        }
//        return indexItems;
//    }

    /**
     * Method used for test purposes to compare expected and actual sites results
     */
    @Override
    public String toString() {
        return "SimpleIndex{" +
                "sites=" + sites +
                "}";
    }
}
