
import Main.Indexes.Index;
import Main.Indexes.SimpleIndex;
import Main.Website;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleIndexTest {
    private Index fullSimpleIndex = null;
    private Index minSimpleIndex = null;
    private Index emptySimpleIndex = null;

    @BeforeEach
    void setUp() {
        fullSimpleIndex = new SimpleIndex();
        minSimpleIndex = new SimpleIndex();
        minSimpleIndex.build(Arrays.asList(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word6", "word7"))));
        emptySimpleIndex = new SimpleIndex();
        List<Website> sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word6", "word7")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3", "word6", "word7","word8")));
        sites.add(new Website("example3.com","example3", Arrays.asList("word2","word3","word4","word5","word6", "word7")));
        fullSimpleIndex.build(sites);
    }

    @AfterEach
    void tearDown() {
        fullSimpleIndex = null;
        minSimpleIndex = null;
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
        assertEquals("SimpleIndex{sites=[]}", emptySimpleIndex.toString(), "Case 1a failed");
        assertEquals("SimpleIndex{sites=[Title: example1\nurl: example1.com\nwords: word1; word2; word6; word7\n]}",
                minSimpleIndex.toString(), "Case 2a failed");
        assertEquals("SimpleIndex{sites=[" +
                        "Title: example1\nurl: example1.com\nwords: word1; word2; word6; word7\n, " +
                        "Title: example2\nurl: example2.com\nwords: word2; word3; word6; word7; word8\n, " +
                        "Title: example3\nurl: example3.com\nwords: word2; word3; word4; word5; word6; word7\n]}",
                fullSimpleIndex.toString(), "Case 3a failed");
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
        lookupMin(minSimpleIndex);
        lookupEmpty(emptySimpleIndex);
	}

    private void lookupMin(Index index) {
        assertEquals(1, index.lookup("word1").size(), "Case 1c failed");
        assertEquals(1, index.lookup("word1 word2").size(), "Case 2c failed");
        assertEquals(1, index.lookup("word1 OR word8").size(), "Case 3c failed");
        assertEquals(0, index.lookup("wordX OR wordY").size(), "Case 4c failed");
    }

    private void lookupEmpty(Index index){
        assertEquals(0, index.lookup("word1").size(), "Case 5c failed");
    }
}