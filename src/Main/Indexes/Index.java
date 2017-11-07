package Main.Indexes;

import Main.IndexMethods;
import Main.Website;

import javax.xml.bind.Element;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This interface can preprocesses a list of website objects and can returns a list of website
 objects matching a query provided as its input
 * Author: Rasmus F
 */
public interface Index {

    /**
     * This method pre-processes a list of string (to ensure faster queries)
     * @param websiteList The full collection of websites that should be processed
     */
    void build(List<Website> websiteList);

    /**
     * This method finds a list of websites that matches the query word (multiple words not allowed)
     * @param query Input string. Depending on the implementation it might allow multiple words and AND and OR statements.
     * @return A collection of matching websites
     */
    List<Website> lookup(String query);

    /**
     * This methods checks if the query input is valid to use for the lookup method.
     * @param query The query to test
     * @return true if the query is valid
     */
    Boolean validateQuery(String query);
}
