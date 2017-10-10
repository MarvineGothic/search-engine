import java.util.*;

public class SimpleIndex implements Index {

    public static void main(String[] args) {

    }

    private List<Website> sites;

    SimpleIndex() {
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

    /**
     *
     * @param query Input string. Depending on the implementation it might allow multiple words and AND and OR statements.
     * @return newList with Website objects that contains query word.
     */
    @Override
    public List<Website> lookup(String query) {
        List<Website> newList = new ArrayList<>();
        // Go through all websites and check if word is present
        for (Website website : sites) {
            if (website.containsWord(query)) {
                newList.add(website);
            }
        }
        return newList;
    }

    @Override
    public Boolean validateQuery(String query) {
        return query.replaceAll("[^a-zA-Z0-9]", "").length() >= query.length();
    }
}