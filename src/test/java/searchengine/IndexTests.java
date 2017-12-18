package searchengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Indexes.InvertedTreeMapIndex;
import searchengine.Indexes.SimpleIndex;
import searchengine.Ranking.Score;
import searchengine.Ranking.SimpleScore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexTests {
    private List<Index> Indexes = null;
    private Score ranker = new SimpleScore();

    @BeforeEach
    void setUp() {
        List<Website> sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word6", "word7")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3", "word6", "word7", "word8")));
        sites.add(new Website("example3.com", "example3", Arrays.asList("word2", "word3", "word4", "word5", "word6", "word7")));
        Indexes = new ArrayList<>();
        Indexes.add(new SimpleIndex());
        Indexes.add(new InvertedHashMapIndex());
        Indexes.add(new InvertedTreeMapIndex());
        for (Index index : Indexes) {
            index.build(sites);
        }
    }

    @AfterEach
    void tearDown() {
        Indexes = null;
    }

    /**
     * Tests the lookup method of each type of Index: SimpleIndex, InvertedHashMapIndex and InvertedTreeMapIndex.
     * <p>
     * Case 1-4b: Single query word
     * Asserts correctness of search using one query word with 1, 2, 3 and 0 matches.
     * Case 1b: a single query word with 1 search result
     * Case 2b: a single query word with 2 search results
     * Case 3b: a single query word with 3 search results
     * Case 4b: a single query word with 0 search results
     * <p>
     * Case 4-9b: Multiple query words
     * Asserts correctness of search using two query word with 1, 2, 3 and 0 matches and 3 query words with 1 match.
     * Case 5b: two query words with 1 search result
     * Case 6b: two query words with 2 search results
     * Case 7b: two query words with 3 search results
     * Case 8b: two query words with 0 search results
     * Case 9b: three query words with 1 search result
     * <p>
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
    void lookupIndex() {
        lookupFull(Indexes);
    }

    private void lookupFull(List<Index> indexList) {
        for (Index index : indexList) {
            // Tests each Index type using 1 query word
            assertEquals(1, index.lookup("word1").size(), "Case 1b failed for " + index);
            assertEquals(2, index.lookup("word3").size(), "Case 2b failed for " + index);
            assertEquals(3, index.lookup("word2").size(), "Case 3b failed for " + index);
            assertEquals(0, index.lookup("wordX").size(), "Case 4b failed for " + index);

            // Tests each Index type using multiple query words
            assertEquals(1, QueryHandler.multiWordQuery(index, "word1 word2", ranker).size(),
                    "Case 5b failed for " + index);
            assertEquals(2, QueryHandler.multiWordQuery(index, "word2 word3", ranker).size(),
                    "Case 6b failed for " + index);
            assertEquals(3, QueryHandler.multiWordQuery(index, "word2 word6", ranker).size(),
                    "Case 7b failed for " + index);
            assertEquals(0, QueryHandler.multiWordQuery(index, "word1 word4", ranker).size(),
                    "Case 8b failed for " + index);
            assertEquals(1, QueryHandler.multiWordQuery(index, "word1 word2 word7", ranker).size(),
                    "Case 9b failed for " + index);

            // Tests each Index type using varying number of query words and OR statements
            assertEquals(2, QueryHandler.multiWordQuery(index, "word1 OR word8", ranker).size(),
                    "Case 10b failed " + index);
            assertEquals(2, QueryHandler.multiWordQuery(index, "word3 OR word4", ranker).size(),
                    "Case 11b failed " + index);
            assertEquals(3, QueryHandler.multiWordQuery(index, "word1 word2 OR word3", ranker).size(),
                    "Case 12b failed " + index);
            assertEquals(1, QueryHandler.multiWordQuery(index, "word1 word7 OR wordX", ranker).size(),
                    "Case 13b failed " + index);
            assertEquals(2, QueryHandler.multiWordQuery(index, "word1 OR word8 OR wordX", ranker).size(),
                    "Case 13b failed " + index);
        }
    }
}