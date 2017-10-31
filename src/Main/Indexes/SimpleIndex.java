package Main.Indexes;

import Main.IndexMethods;
import Main.Website;

import java.util.*;

public class SimpleIndex implements Index {

    public static void main(String[] args) {

    }

    private List<Website> sites;

    public SimpleIndex() {
        this.sites = new ArrayList<>();
    }

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
    public List<Website> singleLookup(String queryWord) {
        List<Website> newList = new ArrayList<>();
        // Go through all websites and check if word is present
        for (Website website : sites) {
            if (website.containsWord(queryWord)) {
                newList.add(website);
            }
        }
        return newList;
    }

    @Override
    public Boolean validateQuery(String query) {
        return query.replaceAll("[^a-zA-Z 0-9]", "").length() >= query.length();
    }
}
