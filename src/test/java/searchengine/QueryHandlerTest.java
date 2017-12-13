package searchengine;

import searchengine.Indexes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Ranking.IRanker;
import searchengine.Ranking.RankerIDF;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

    class QueryHandlerTest {
        private IndexMethods indexMethods = null;
        private Index idx;
        private IRanker ranker;

        @BeforeEach
        void setUp() {
            List<Website> sites = new ArrayList<>();
            sites.add(new Website("1.com","example1", Arrays.asList("word1", "word2")));
            sites.add(new Website("2.com","example2", Arrays.asList("word2", "word3")));
            sites.add(new Website("4.com","example4", Arrays.asList("word3", "word4", "word5")));
            sites.add(new Website("5.com","example5", Arrays.asList("word6", "word7", "word8", "word9", "word10", "word11")));
            sites.add(new Website("6.com","example6", Arrays.asList("OROROR", "word20")));
            sites.add(new Website("7.com","example7", Arrays.asList("Denmark", "Germany")));

            sites.add(new Website("8.com","example8", Arrays.asList("w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w00" )));
            sites.add(new Website("8.com","example8", Arrays.asList("w1", "w0", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w00" )));
            sites.add(new Website("8.com","example8", Arrays.asList("w1", "w0", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w" )));
            sites.add(new Website("8.com","example8", Arrays.asList("w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w00" )));
            sites.add(new Website("8.com","example8", Arrays.asList("w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w12" )));
            sites.add(new Website("8.com","example8", Arrays.asList("w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w12" )));
            sites.add(new Website("8.com","example8", Arrays.asList("w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w" )));
            idx = new SimpleIndex();
            idx.build(sites);
            ranker = new RankerIDF(sites);
            indexMethods = new IndexMethods();
        }

        @Test
        void testSingleWord() {
            assertEquals(1, IndexMethods.multiWordQuery(idx, "word1", ranker).size());
            assertEquals("example1", IndexMethods.multiWordQuery(idx,"word1", ranker).get(0).getTitle());
            assertEquals(2, IndexMethods.multiWordQuery(idx,"word2", ranker).size());
        }

        @Test
        void testMultipleWords() {
            assertEquals(1, IndexMethods.multiWordQuery(idx,"word1 word2", ranker).size());
            assertEquals(1, IndexMethods.multiWordQuery(idx,"word2 word1", ranker).size());
            assertEquals(1, IndexMethods.multiWordQuery(idx,"word3 word4",ranker).size());
            assertEquals(1, IndexMethods.multiWordQuery(idx,"word4 word3 word5", ranker).size());
            // checks if only all words are in the website
            assertEquals(0, IndexMethods.multiWordQuery(idx,"word1 word3", ranker).size());
            assertEquals(0, IndexMethods.multiWordQuery(idx,"word4 word1 word5", ranker).size());
            assertEquals(2, IndexMethods.multiWordQuery(idx,"w1 w2 w3 w12", ranker).size());
            assertEquals(2, IndexMethods.multiWordQuery(idx,"w12 w2 w3 w1", ranker).size());
            assertEquals(1, IndexMethods.multiWordQuery(idx,"w w0 w3", ranker).size());
            assertEquals(7, IndexMethods.multiWordQuery(idx,"w6 w5 w5", ranker).size());
        }

        @Test
        void testORQueries() {
            assertEquals(3, IndexMethods.multiWordQuery(idx,"word2 OR word3", ranker).size());
            assertEquals(2, IndexMethods.multiWordQuery(idx,"word1 OR word4", ranker).size());
            // Corner case: Does code remove duplicates?
            assertEquals(1, IndexMethods.multiWordQuery(idx,"word1 OR word1", ranker).size());
        }
        @Test
        void testMultipleORQueries() {
            assertEquals(4, IndexMethods.multiWordQuery(idx,"word2 OR word3 OR word5 OR word6", ranker).size());
            assertEquals(2, IndexMethods.multiWordQuery(idx,"word1 OR word4 OR ", ranker).size());
            // Corner case: Does code remove duplicates?
            assertEquals(1, IndexMethods.multiWordQuery(idx,"word1 OR word1 OR OR word1", ranker).size());
            assertEquals(7, IndexMethods.multiWordQuery(idx,"w1 w2 w3 w12 OR w3 w0 w OR w4 w5 w6", ranker).size());
            assertEquals(3, IndexMethods.multiWordQuery(idx,"w1 w2 w3 w12 OR w3 w0 w", ranker).size());
        }

        /**
         *  Test for problematic input
         *  Case 1a: No word after OR
         *  Case 2a: Use of both upper- and lower-case letters in query elements
         */
        @Test
        void testCornerCases() {
            assertEquals(1, IndexMethods.multiWordQuery(idx, "word1 OR ", ranker).size(), "Case 1a");
            assertEquals(1, IndexMethods.multiWordQuery(idx, "Denmark OR germany", ranker).size(), "Case 2a");
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
                "w1 w2 w3 w12 OR w3 w0 w OR w4 w5 w6",
                "w1 w2 w3 w12 OR w3 w0 w",
                "w1 w2 w3 w12",
                "w12 w2 w3 w1",
                "w w0 w3",
                "w6 w5 w5",
        };
        for (String lookupQuery : lookupQueries) {
            List<Website> expected = IndexMethods.multiWordQuery(idx, lookupQuery, ranker);
            List<Website> actual = IndexMethods.multiWordQuery2(idx, lookupQuery, ranker);
            assertEquals(expected, actual, "Failed test for query: " + lookupQuery);
        }
    }

}