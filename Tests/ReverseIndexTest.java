
import Main.Indexes.Index;
import Main.Indexes.ReverseHashMapIndex;
import Main.Website;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReverseIndexTest {
    private Index fullHashMapIndex = null;
    private Index minHashMapIndex = null;
    private Index emptyHashMapIndex = null;

    @BeforeEach
    /**
     * Instantiates three indexes used for testing:
     * fullHashMapIndex: ReverseHashMapIndex with a number of websites all containing a variety of different words
     * minSimpleIndex: SimpleIndex with 1 website
     * emptySimpleIndex: SimpleIndex without websites for testing corner-cases
     */
    void setUp() {
        fullHashMapIndex = new ReverseHashMapIndex();
        minHashMapIndex = new ReverseHashMapIndex();
        minHashMapIndex.build(Arrays.asList(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word6", "word7"))));
        emptyHashMapIndex = new ReverseHashMapIndex();
        List<Website> sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word6", "word7")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3", "word6", "word7","word8")));
        sites.add(new Website("example3.com","example3", Arrays.asList("word2","word3","word4","word5","word6", "word7")));
        fullHashMapIndex.build(sites);
    }

    @AfterEach
    void tearDown() {
        fullHashMapIndex = null;
        minHashMapIndex = null;
    }

    /**
     * Tests the build method of SimpleIndex.
     *
     * Case 1a: Testing if build method crashes if it is called on an empty websiteList
     * Case 2a: Testing if build method works for websiteList containing 1 website
     * Case 3a: Testing if build method works for websiteList containing 3 websites
     */
    @Test
    void buildSimpleIndex() {
        assertEquals("SimpleIndex{sites=[]}", emptyHashMapIndex.toString(), "Case 1a failed");
        assertEquals("SimpleIndex{sites=[Title: example1\nurl: example1.com\nwords: word1; word2; word6; word7\n]}",
                minHashMapIndex.toString(), "Case 2a failed");
        assertEquals("SimpleIndex{sites=[" +
                        "Title: example1\nurl: example1.com\nwords: word1; word2; word6; word7\n, " +
                        "Title: example2\nurl: example2.com\nwords: word2; word3; word6; word7; word8\n, " +
                        "Title: example3\nurl: example3.com\nwords: word2; word3; word4; word5; word6; word7\n]}",
                fullHashMapIndex.toString(), "Case 3a failed");
    }

    /**
     * Testing if the validateQuery method creates false positives or false negatives. Cases tested for false negatives:
     * Spaces, tabs and numbers.
     * Cases tested for false positives:
     * A variety of commonly used punctuation
     */
    @Test
    void ValidateQuerySimpleIndex() {
        String[] validQueries = {"word1", "testTwo", "test three", "test 4 ", "   test  fi ve"};
        String[] invalidQueries = {".",",",":","?","!","/","[","]","{","}","(",")",};
        for (String query : validQueries) {
            assertTrue(fullHashMapIndex.validateQuery(query), "Test failed for query: " + query);
        }
        for (String query : invalidQueries) {
            assertFalse(fullHashMapIndex.validateQuery(query), "Test failed for query: " + query);
        }
    }

    /**
     * Testing corner cases for the SimpleIndex class
     *
     * Case 1-5c: testing corner-cases for the lookup method
     * Case 1c: single word query match for websiteList with only one website
     * Case 2c: two word query match for websiteList with only one website
     * Case 3c: two word query with OR-statement match for websiteList with only one website
     * Case 4c: two word query with OR-statement match for websiteList with only one website and no matches
     * Case 5c: check for crash if websiteList is empty
     */
    @Test
    void testCornerCases(){
        lookupMin(minHashMapIndex);
        lookupEmpty(emptyHashMapIndex);
    }

    private void lookupMin(Index index) {
        assertEquals(1, index.lookup("word1").size(), "Case 1a failed");
        assertEquals(1, index.lookup("word1 word2").size(), "Case 2a failed");
        assertEquals(1, index.lookup("word1 OR word8").size(), "Case 3a failed");
        assertEquals(0, index.lookup("wordX OR wordY").size(), "Case 4a failed");
    }

    private void lookupEmpty(Index index){
        assertEquals(0, index.lookup("word1").size(), "Case 5a failed");
    }
}