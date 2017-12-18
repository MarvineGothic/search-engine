package searchengine;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static searchengine.QueryHandler.splitQuery;

class SplitQueryTest {

    @Test
    void testSingleWord() {
        assertEquals(1, splitQuery("word1").size());
        assertEquals(1, splitQuery("word2").size());
        assertEquals(0, splitQuery("").size());
        assertEquals(0, splitQuery(" ").size());
        assertEquals(0, splitQuery("  ").size());
        assertEquals("word1", splitQuery(" word1").get(0).get(0));
        assertEquals("word1", splitQuery("  word1").get(0).get(0));
        assertEquals("word1", splitQuery("word1 ").get(0).get(0));
        assertEquals("word1", splitQuery("  word1  ").get(0).get(0));
    }

    @Test
    void testMultipleWords() {
        assertEquals(1, splitQuery("word1 word2").size());
        assertEquals("word1", splitQuery("word1 word2").get(0).get(0));
        assertEquals("word2", splitQuery("word1 word2").get(0).get(1));

        assertEquals(1, splitQuery(" word3 word4").size());
        assertEquals(1, splitQuery("  word4  word3 word5").size());
        assertEquals(1, splitQuery("word1  word3").size());

        assertEquals(1, splitQuery("  word4  word1  word5  ").size());
        assertEquals("word4", splitQuery("  word4  word1  word5  ").get(0).get(0));
        assertEquals("word1", splitQuery("  word4  word1  word5  ").get(0).get(1));
        assertEquals("[[word4, word1, word5]]", splitQuery("  word4  word1  word5  ").toString());
    }

    @Test
    void testORQueries() {
        assertEquals(2, splitQuery("word2 OR word3").size());
        assertEquals(2, splitQuery("word1 OR word4").size());
        assertEquals(1, splitQuery("word1 OR word1").size());

        assertEquals(1, splitQuery(" OR word1").size());
        assertEquals("word1", splitQuery(" OR word1").get(0).get(0));
        assertEquals(1, splitQuery("word1 OR ").size());
        assertEquals(1, splitQuery("word1 OR   ").size());
        assertEquals(0, splitQuery("   OR   ").size());
    }

    @Test
    void testMultipleORQueries() {
        assertEquals(4, splitQuery("word2 OR word3 OR word5 OR word6").size());
        assertEquals("[[word2], [word3], [word5], [word6]]", splitQuery("word2 OR word3 OR word5 OR word6").toString());
        assertEquals(2, splitQuery("word1 OR word4 OR ").size());
        // Corner case: Does code remove duplicates?
        assertEquals(1, splitQuery("word1 OR word1 OR word1").size());
    }

    // Test for problematic input
    @Test
    void testCornerCases() {
        // spaces and punctuation
        assertEquals(1, splitQuery(" word1,").size());
        assertEquals(0, splitQuery(" ").size());
        assertEquals(1, splitQuery(". ").size());
        assertEquals("[[.]]", splitQuery(". ").toString());
        assertEquals(1, splitQuery("word1 OR ").size());
        // mistaken query
        assertEquals(1, QueryHandler.splitQuery(" OR OROROR OR ").size());
        assertEquals(2, splitQuery("Denmark OR Germany").size());
        assertEquals("Denmark", splitQuery("Denmark OR Germany").get(0).get(0));
        assertEquals("denmark", QueryHandler.modifyQuery(splitQuery("Denmark OR Germany")).get(0).get(0));

        // spaces and punctuation
        assertEquals(1, QueryHandler.modifyQuery(splitQuery(" word1,")).size());
        assertEquals(0, QueryHandler.modifyQuery(splitQuery(" ")).size());
        assertEquals("[]", QueryHandler.modifyQuery(splitQuery(" ")).toString());
        assertEquals("[]", QueryHandler.modifyQuery(splitQuery(". ")).toString());
        assertEquals(0, QueryHandler.modifyQuery(splitQuery(". ")).size());
    }
}