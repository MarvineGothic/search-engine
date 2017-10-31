import Main.Indexes.SimpleIndex;
import Main.FileHelper;
import Main.Website;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleIndexTest {

    /**
     * Tests the correctness of validation results of the validateQueryResults method using an array of queries
     * with an expected return value of true, named validQueries.
     */
    @Test
    void Should_ReturnTrue_When_QueryContainsOnlyCharactersNumbersOrSpaces() {
        SimpleIndex testIndex = new SimpleIndex();
        String[] validQueries = {"testone", "testTwo", "test three", "test 4 ", "   test fi ve"};
        for (String query : validQueries) {
            assertTrue(testIndex.validateQuery(query), "Query failed: " + query);
        }
    }

    /**
     * Tests the correctness of validation results of the validateQueryResults method using an array of queries with an
     * expected return value of false, named invalidQueries.
     */
    @Test
    void Should_ReturnFalse_When_QueryContainsPunctuation() {
        SimpleIndex testIndex = new SimpleIndex();
        String[] invalidQueries = {".", ","};
        for (String query : invalidQueries) {
            assertFalse(testIndex.validateQuery(query), "Query fails: " + query);
        }
    }

    /**
     *
     */
    @Test
    // Test of lookUp
    void Should_ReturnWebsiteInList_When_QueryMatchesWordInWebsite() {
        SimpleIndex testIndex = new SimpleIndex();

        // Empty websiteList
        List<Website> websiteList0 = Arrays.asList();

        // websiteList with one website
        List<Website> webSiteList1 = Arrays.asList(
                new Website("standardTestSite1.org", "testTitle1", Arrays.asList("apple", "pear", "mango")));

        // websiteList with two websites
        List<Website> webSiteList2 = Arrays.asList(
                new Website("standardTestSite1.org", "testTitle1", Arrays.asList("apple", "pear", "mango")),
                new Website("standardTestSite2.org", "testTitle2", Arrays.asList("apple", "mango", "plum")));

        // websiteList with three websites
        List<Website> webSiteList3 = Arrays.asList(
                new Website("standardTestSite1.org", "testTitle1", Arrays.asList("apple", "pear", "mango")),
                new Website("standardTestSite2.org", "testTitle2", Arrays.asList("apple", "mango", "plum")),
                new Website("standardTestSite3.org", "testTitle3", Arrays.asList("apple", "mandarin", "clementine")));

        // List of all websiteLists
        List<List<Website>> listOfWebsiteLists = Arrays.asList(websiteList0, webSiteList1, webSiteList2, webSiteList3);

        // Test for query word Apple - Should match all websites in all websiteLists
        for (List<Website> listOfWebsites: listOfWebsiteLists) {
            testIndex.build(listOfWebsites);
            assertEquals(listOfWebsites ,testIndex.lookup("apple"), "List failed: "+listOfWebsites );
            }

        // Test for query word Mango - Should match website 1 in webSiteList1 and website 2 in webSiteList1 and webSiteList2
        for (List<Website> listOfWebsites: listOfWebsiteLists){
            testIndex.build(listOfWebsites);
            assertEquals(, testIndex.lookup("mango"), "List failed: "+listOfWebsites);

        }




    }

        /**
         * Tests if the index finds the expected pages from expected query words
         */
        @Test
        void assertExpectedSearchResults () {
            // TODO: 24-Oct-17 Currently fails due to capital letters in query words
            // A  list of websites to build the indexes
            String path = System.getProperty("user.dir") + File.separator + "TestData" + File.separator + "expectedQueryTest.txt";
            List<Website> listOfWebsites = FileHelper.parseFile(path);

            // A list of query words along with the expected title of the sites found
            Map<String, String[]> expectedSitesFound = new HashMap<>();
            expectedSitesFound.put("apple", new String[]{"Site1", "Site2"});
            expectedSitesFound.put("banana", new String[]{"Site1", "Site2"});
            expectedSitesFound.put("Car", new String[]{"Site3", "Site6", "Site7"});
            expectedSitesFound.put("Airplane", new String[]{"Site3"});
            expectedSitesFound.put("laptop", new String[]{"Site4"});
            expectedSitesFound.put("Unknown", new String[0]);
            expectedSitesFound.put("empty", new String[0]);

            SimpleIndex s = new SimpleIndex();
            // Checks each entry of expectedSitesFound if the found query is the expected one.
            for (Map.Entry<String, String[]> pair : expectedSitesFound.entrySet()) {
                List<Website> queryResult = s.lookup(pair.getKey());
                Collections.sort(queryResult);
                int expectedPagesFound = pair.getValue().length;

                // Checks expected length
                assertEquals(expectedPagesFound, queryResult.size());

                // Checks expected words found
                for (int j = 0; j < queryResult.size(); j++) {
                    Website website = queryResult.get(j);
                    assertEquals(website.getTitle(), pair.getValue()[j]);
                }
            }
        }
    }

