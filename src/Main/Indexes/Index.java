package Main.Indexes;

import Main.IndexMethods;
import Main.Website;

import java.util.List;

/**
 * This interface can preprocesses a list of website objects and can returns a list of website
 objects matching a query provided as its input
 * Author: Rasmus F
 */
public interface Index {

    /**
     * This method pre-processes a list of string (to ensure faster queries)
     * @param websiteList The full list of websites that should be processed
     */
    void build(List<Website> websiteList);

    /**
     * This method finds a list of websites that matches the query
     * @param query Input string. Depending on the implementation it might allow multiple words and AND and OR statements.
     * @return A list of matching websites
     */
    default List<Website> lookup(String query) {
        return IndexMethods.multiWordQuery(this, query);
    }

    /**
     * This methods finds a list of websites that matches the query of a single word
     * @param queryWord The word you want to query
     * @return A list of matching websites
     */
    List<Website> singleLookup(String queryWord);

    /**
     * This methods checks if the query input is valid to use for the lookup method.
     * @param query The query to test
     * @return true if the query is valid
     */
    Boolean validateQuery(String query);
}
