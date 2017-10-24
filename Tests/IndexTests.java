import Main.Indexes.Index;
import Main.Indexes.ReverseHashMapIndex;
import Main.Indexes.ReverseTreeMapIndex;
import Main.Indexes.SimpleIndex;
import Main.FileHelper;
import Main.Website;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class IndexTests {

    /**
     * Test if the if the the SimpleIndex, ReverseTreeMapIndex and ReverseHashMapIndex finds the same pages on lookup
     */
    @Test
    void assertSameSearchResults() {

        // A list of word to use as queries
        Set<String> words = FileHelper.loadWordsInFile("enwiki-tiny.txt");

        // A  list of websites to build the indexes
        List<Website> listOfWebsites = FileHelper.loadFile("enwiki-small.txt");

        // A list of indexes to test
        List<Index> Indexes = new ArrayList<>();
        Indexes.add(new ReverseHashMapIndex());
        Indexes.add(new ReverseTreeMapIndex());
        Indexes.add(new SimpleIndex());

        // Build all the indexes
        for (Index index : Indexes) {
            index.build(listOfWebsites);
        }

        for (String word : words) {
            // The search result for the current query word. In order to compare safely the list of websites are sorted
            // This value is the results all other indexes are compared to.
            List<Website> checkList = Indexes.get(0).lookup(word);
            Collections.sort(checkList);

            // Loop over all other indexes and compare the sorted list of websites found
            for (int i = 1; i < Indexes.size(); i++) {
                Index index = Indexes.get(i);
                List<Website> testList = index.lookup(word);
                Collections.sort(testList);
                assertEquals(testList, checkList);
            }
        }
    }



    /**
     * Test if the if the the Indexes finds the expected pages on specific queries
     */
    @Test
    void assertExpectedSearchResults() {

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

        // A list of indexes to test
        List<Index> Indexes = new ArrayList<>();
        Indexes.add(new ReverseHashMapIndex());
        Indexes.add(new ReverseTreeMapIndex());
        Indexes.add(new SimpleIndex());

        // Build all the indexes
        for (Index index : Indexes) {
            index.build(listOfWebsites);
        }

        for (Index index : Indexes) {
            // Checks each entry of expectedSitesFound if the found query is the expected one.
            for (Map.Entry<String, String[]> pair : expectedSitesFound.entrySet()) {
                List<Website> queryResult = index.lookup(pair.getKey());
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
}