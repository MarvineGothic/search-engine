package searchengine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static searchengine.QueryHandler.splitQuery;

/**
 * <pre>
 * This test tests particular a splitQuery and a modifyQuery methods from QueryHandler class.
 * </pre>
 */
class SplitQueryTest {

    /**
     * <pre>
     * Testing how splitQuery processing a query with a single word with a whitespace or a whitespace only
     * </pre>
     */
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

    /**
     * <pre>
     * Testing how splitQuery splits a query by a whitespaces
     * </pre>
     */
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
        System.out.println(splitQuery("  word4  word1  word5  ").toString());
        assertEquals("[[word4, word1, word5]]", splitQuery("  word4  word1  word5  ").toString());
    }

    /**
     * <pre>
     * Testing how splitQuery splits a query by a keyword "OR"
     * </pre>
     */
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

    /**
     * <pre>
     * Testing how splitQuery splits a query by multiple keyword "OR"
     * </pre>
     */
    @Test
    void testMultipleORQueries() {
        assertEquals(4, splitQuery("word2 OR word3 OR word5 OR word6").size());
        assertEquals("[[word2], [word3], [word5], [word6]]", splitQuery("word2 OR word3 OR word5 OR word6").toString());
        assertEquals(4, splitQuery("word2 word3 OR word3 word4 OR word5 word6 OR word6 word7").size());
        assertEquals("[[word2, word3], [word3, word4], [word5, word6], [word6, word7]]",
                splitQuery("word2 word3 OR word3 word4 OR word5 word6 OR word6 word7").toString());
        assertEquals(2, splitQuery("word1 OR word4 OR ").size());
        // Corner case: Does code remove duplicates?
        assertEquals(1, splitQuery("word1 OR word1 OR word1").size());
    }

    /**
     * <pre>
     * Testing corner cases for splitQuery and modifyQuery methods
     * Case if no words and only whitespaces are in the query, then size of query is 0
     * Case if only punctuation for splitQuery the size is 1, but for modifyQuery is 0
     * Case if the query has a low- or high case letters, splitQuery is sensitive and modifyQuery is not
     * Case if query is only a word with a keyword "OR" or a punctuation and whitespaces, then only a word left
     * after processing the query
     * </pre>
     */
    @Test
    void testCornerCases() {
        // spaces and punctuation
        assertEquals(1, splitQuery(" word1,").size());
        assertEquals(0, splitQuery(" ").size());
        assertEquals(1, splitQuery(". ").size());
        assertEquals("[[.]]", splitQuery(". ").toString());
        assertEquals(1, splitQuery("word1 OR ").size());
        assertEquals("word1", splitQuery("word1 OR ").get(0).get(0));


        assertEquals(1, QueryHandler.splitQuery(" OR OROROR OR ").size());
        assertEquals(2, splitQuery("Denmark OR Germany").size());
        assertEquals("Denmark", splitQuery("Denmark OR Germany").get(0).get(0));
        assertEquals("denmark", splitQuery("denmark OR Germany").get(0).get(0));

        assertEquals("denmark", QueryHandler.modifyQuery(splitQuery("Denmark OR Germany")).get(0).get(0));
        assertEquals("denmark", QueryHandler.modifyQuery(splitQuery("denmark OR Germany")).get(0).get(0));

        // spaces and punctuation
        assertEquals(1, QueryHandler.modifyQuery(splitQuery(" word1,")).size());
        assertEquals("word1", QueryHandler.modifyQuery(splitQuery(" word1,")).get(0).get(0));
        assertEquals(0, QueryHandler.modifyQuery(splitQuery(" ")).size());
        assertEquals("[]", QueryHandler.modifyQuery(splitQuery(" ")).toString());
        assertEquals("[]", QueryHandler.modifyQuery(splitQuery(". ")).toString());
        assertEquals(0, QueryHandler.modifyQuery(splitQuery(". ")).size());
    }
}