import Main.Indexes.Index;
import Main.Indexes.ReverseHashMapIndex;
import Main.Indexes.ReverseTreeMapIndex;
import Main.Indexes.SimpleIndex;
import Main.Website;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndexTests {
    private List<Index> Indexes = null;

    @BeforeEach
    void setUp() {
        List<Website> sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word6", "word7")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3", "word6", "word7","word8")));
        sites.add(new Website("example3.com","example3", Arrays.asList("word2","word3","word4","word5","word6", "word7")));
        Indexes = new ArrayList<>();
        Indexes.add(new SimpleIndex());
        Indexes.add(new ReverseHashMapIndex());
        Indexes.add(new ReverseTreeMapIndex());
        for (Index index: Indexes) {
            index.build(sites);
        }
    }

    @AfterEach
    void tearDown() {
        Indexes = null;
    }


    /**
     * Tests the lookup method of each type of Index: SimpleIndex, ReverseHashMapIndex and ReverseTreeMapIndex.
     * The FileHelper class rids data input of duplicate words in websites, so there are no tests for duplicate words.
     *
     * Case 1-4b: Single query word
     * Asserts correctness of search using one query word with 1, 2, 3 and 0 matches.
     * Case 1b: a single query word with 1 search result
     * Case 2b: a single query word with 2 search results
     * Case 3b: a single query word with 3 search results
     * Case 4b: a single query word with 0 search results
     *
     * Case 4-9b: Multiple query words
     * Asserts correctness of search using two query word with 1, 2, 3 and 0 matches and 3 query words with 1 match.
     * Case 5b: two query words with 1 search result
     * Case 6b: two query words with 2 search results
     * Case 7b: two query words with 3 search results
     * Case 8b: two query words with 0 search results
     * Case 9b: three query words with 1 search result
     *
     * Case 10-13b: Single and multiple query words using OR statements
     * Asserts correctness of search using single and multiple query words with varying matches while using OR statements
     * Case 10b: OR statement with two query words with 1 search result each, totaling 2 results
     * Case 11b: OR statement with one query word with 1 search result, and one with two search results, but only 2
     * individual websites
     * Case 12b: two query words connected to a third with an OR statement. Should return all websites
     * Case 13b: two query words connected to a third with an OR statement. The single query word returns no websites,
     * while the two return 1
     */
    @Test
    void lookupSimpleIndex() {
        lookupFull(Indexes);
    }

    private void lookupFull(List<Index> indexList) {
        for (Index index:indexList) {
        // Tests each Index type using 1 query word
        assertEquals(1, index.lookup("word1").size(), "Case 1b failed for "+index);
        assertEquals(2, index.lookup("word3").size(), "Case 2b failed for "+index);
        assertEquals(3, index.lookup("word2").size(), "Case 3b failed for "+index);
        assertEquals(0, index.lookup("wordX").size(), "Case 4b failed for "+index);

        // Tests each Index type using multiple query words
        assertEquals(1, index.lookup("word1 word2").size(), "Case 5b failed for "+index);
        assertEquals(2, index.lookup("word2 word3").size(), "Case 6b failed for "+index);
        assertEquals(3, index.lookup("word2 word6").size(), "Case 7b failed for "+index);
        assertEquals(0, index.lookup("word1 word4").size(), "Case 8b failed for "+index);
        assertEquals(1, index.lookup("word1 word2 word7").size(), "Case 9b failed for "+index);

        // Tests each Index type using varying number of query words and OR statements
        assertEquals(2, index.lookup("word1 OR word8").size(), "Case 10b failed "+index);
        assertEquals(2, index.lookup("word3 OR word4").size(), "Case 11b failed "+index);
        assertEquals(3, index.lookup("word1 word2 OR word3").size(), "Case 12b failed "+index);
        assertEquals(1, index.lookup("word1 word7 OR wordX").size(), "Case 13b failed "+index);
        assertEquals(2, index.lookup("word1 OR word8 OR wordX").size(), "Case 13b failed "+index);
        }
    }

    /**
     * Testing if the validateQuery method creates false positives or false negatives. Cases tested for false negatives:
     * Spaces, tabs and numbers.
     * Cases tested for false positives:
     * A variety of commonly used punctuation
     */
    @Test
    void TestValidateQuery() {
        for (Index index:Indexes) {
            // Define valid query cases
            String[] validQueries = {"word1", "testTwo", "test three", "test 4 ", "   test  fi ve"};
            // Define invalid query cases
            String[] invalidQueries = {".",",",":","?","!","/","[","]","{","}","(",")",};
            // Test valid query cases for each Index type
            for (String query : validQueries) {
                assertTrue(index.validateQuery(query), "Test failed for query: " + query + " using the "+index);
            }
            //Test invalid query cases for each Index type
            for (String query : invalidQueries) {
                assertFalse(index.validateQuery(query), "Test failed for query: " + query + " using the "+index);
            }
        }
    }

}