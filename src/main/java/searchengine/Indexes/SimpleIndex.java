package searchengine.Indexes;

import searchengine.Website;

import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * Implements the Index interface using a simplest possible algorithm
 * </pre>
 */
public class SimpleIndex implements Index {
    private List<Website> sites;

    public SimpleIndex() {
        this.sites = new ArrayList<>();
    }

    /**
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
        for (Website website : sites) {
            if (website.containsWord(queryWord)) {
                newList.add(website);
            }
        }
        return newList;
    }

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
