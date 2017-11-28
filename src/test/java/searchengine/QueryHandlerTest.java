package searchengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Indexes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryHandlerTest {
    private IndexMethods indexMethods = null;
    private Index idx;
    private IRanker ranker = new NoRanker();
    private IRanker ranker2;

    @BeforeEach
    void setUp() {
        List<Website> sites = new ArrayList<>();
        sites.add(new Website("1.com", "example1", Arrays.asList("word1", "word2")));
        sites.add(new Website("2.com", "example2", Arrays.asList("word2", "word3")));
        sites.add(new Website("4.com", "example4", Arrays.asList("word3", "word4", "word5")));
        sites.add(new Website("5.com", "example5", Arrays.asList("word6", "word7", "word8", "word9", "word10", "word11")));
        sites.add(new Website("6.com", "example6", Arrays.asList("OROROR", "word20")));
        sites.add(new Website("7.com", "example7", Arrays.asList("Denmark", "Germany")));
        idx = new SimpleIndex();
        idx.build(sites);
        ranker2 = new RankerBM25(sites);
        indexMethods = new IndexMethods();
    }

    @Test
    void testSingleWord() {
        assertEquals(1, IndexMethods.multiWordQuery(idx, "word1", ranker).size());
        assertEquals("example1", IndexMethods.multiWordQuery(idx, "word1", ranker).get(0).getTitle());
        assertEquals(2, IndexMethods.multiWordQuery(idx, "word2", ranker).size());
    }

    @Test
    void testMultipleWords() {
        assertEquals(1, IndexMethods.multiWordQuery(idx, "word1 word2", ranker).size());
        assertEquals(1, IndexMethods.multiWordQuery(idx, "word3 word4", ranker).size());
        assertEquals(1, IndexMethods.multiWordQuery(idx, "word4 word3 word5", ranker).size());
        // checks if only all words are in the website
        assertEquals(0, IndexMethods.multiWordQuery(idx, "word1 word3", ranker).size());
        assertEquals(0, IndexMethods.multiWordQuery(idx, "word4 word1 word5", ranker).size());
    }

    @Test
    void testORQueries() {
        assertEquals(3, IndexMethods.multiWordQuery(idx, "word2 OR word3", ranker).size());
        assertEquals(2, IndexMethods.multiWordQuery(idx, "word1 OR word4", ranker).size());
        // Corner case: Does code remove duplicates?
        assertEquals(1, IndexMethods.multiWordQuery(idx, "word1 OR word1", ranker).size());
    }

    @Test
    void testMultipleORQueries() {
        assertEquals(4, IndexMethods.multiWordQuery(idx, "word2 OR word3 OR word5 OR word6", ranker).size());
        assertEquals(2, IndexMethods.multiWordQuery(idx, "word1 OR word4 OR ", ranker).size());
        // Corner case: Does code remove duplicates?
        assertEquals(1, IndexMethods.multiWordQuery(idx, "word1 OR word1 OR OR word1", ranker).size());
    }

    // Test for problematic input
    @Test
    void testCornerCases() {
        // spaces and punctuation
        assertEquals(1, IndexMethods.multiWordQuery(idx, " word1,", ranker).size());
        assertEquals(0, IndexMethods.multiWordQuery(idx, " ", ranker).size());
        assertEquals(0, IndexMethods.multiWordQuery(idx, ". ", ranker).size());
        assertEquals(0, IndexMethods.multiWordQuery(idx, " OR OR OR ", ranker).size());
        assertEquals(1, IndexMethods.multiWordQuery(idx, "word1 OR ", ranker).size());
        assertEquals(1, IndexMethods.multiWordQuery(idx, "Denmark OR Germany", ranker).size());
    }


    /**
     * Test that multiWordLookup1 has the same results as multiWordLookup2
     */
    @Test
    void testMultiWordLookupOneVsTwo() {
        String[] lookupQueries = new String[]{
                "word1 OR word2 OR word word3",
                "word2 OR word3",
                "word1 OR word4",
                "word1 OR word1",
                "word2 OR word3 OR word5 OR word6",
                "word1 OR word4 OR ",
                "word1 OR word1 OR OR word1",
                " word1,",
                " ",
                ". ",
                "word1 OR ",
                "Denmark OR Germany",
                "word1 OR word8 OR wordX",
        };
        for (String lookupQuery : lookupQueries) {
            List<Website> expected = IndexMethods.multiWordQuery(idx, lookupQuery, ranker2);
            List<Website> actual = IndexMethods.multiWordQuery2(idx, lookupQuery, ranker2);
            assertEquals(expected, actual, "Failed test for query: " + lookupQuery);
        }
    }

}