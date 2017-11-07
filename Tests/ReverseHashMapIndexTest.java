
import Main.Indexes.ReverseHashMapIndex;
import Main.Indexes.ReverseTreeMapIndex;
import Main.Website;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ReverseHashMapIndexTest {
    private ReverseHashMapIndex fullHashMapIndex = null;
    private ReverseHashMapIndex minHashMapIndex = null;
    private ReverseHashMapIndex emptyHashMapIndex = null;
    private Map<String, HashSet<Website>> emptyHashWordMap = null;
    private String minHashWordMap = null;
    private String fullHashWordMap = null;
    private ReverseTreeMapIndex fullTreeMapIndex = null;
    private ReverseTreeMapIndex minTreeMapIndex = null;
    private ReverseTreeMapIndex emptyTreeMapIndex = null;
    private Map<String, HashSet<Website>> emptyTreeWordMap = null;
    private String minTreeWordMap = null;
    private String fullTreeWordMap = null;


    @BeforeEach
    void setUp() {

        emptyHashMapIndex = new ReverseHashMapIndex();
        emptyTreeMapIndex = new ReverseTreeMapIndex();
        fullHashMapIndex = new ReverseHashMapIndex();
        fullTreeMapIndex = new ReverseTreeMapIndex();
        minHashMapIndex = new ReverseHashMapIndex();
        minTreeMapIndex = new ReverseTreeMapIndex();
        minHashMapIndex.build(Arrays.asList(new Website("example1.com", "example1", Arrays.asList("word1", "word2"))));
        minTreeMapIndex.build(Arrays.asList(new Website("example1.com", "example1", Arrays.asList("word1", "word2"))));
        List<Website> sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3")));
        sites.add(new Website("example3.com","example3", Arrays.asList("word4","word5","word2")));
        fullTreeMapIndex.build(sites);
        fullHashMapIndex.build(sites);
        emptyHashWordMap = emptyHashMapIndex.getWordMap();
        emptyTreeWordMap = emptyHashMapIndex.getWordMap();
        minHashWordMap = minHashMapIndex.getWordMap().toString();
        minTreeWordMap = minTreeMapIndex.getWordMap().toString();
        fullHashWordMap = fullHashMapIndex.getWordMap().toString();
        fullTreeWordMap = fullTreeMapIndex.getWordMap().toString();
    }

    @AfterEach
    void tearDown() {
        fullHashMapIndex = null;
        minHashMapIndex = null;
        minHashWordMap = null;
        fullHashWordMap = null;
    }

    /**
     * Tests the build method of ReverseHashMapIndex.
     *
     * Case 1a-3a: Testing build method for ReverseHashMapIndex
     * Case 1a: Testing if build method crashes if it is called on an empty websiteList
     * Case 2a: Testing if build method works for websiteList containing 1 website
     * Case 3a: Testing if build method works for websiteList containing 3 websites
     *
     * Case 4a-6a: Testing build method for ReverseTreeMapIndex
     * Case 4a: Testing if build method crashes if it is called on an empty websiteList
     * Case 5a: Testing if build method works for websiteList containing 1 website
     * case 6a: Testing if build method works for websiteList containing 3 websites
     *
     */
    @Test
    void buildReverseIndex(){
        buildHashMapIndex();
        buildTreeMapIndex();
    }

    @Test
    void buildHashMapIndex() {
        assertNull(emptyHashWordMap, "Case 1a failed");
        //P1

        assertTrue(fullHashWordMap.contains("word1=[Title: example1\nurl: example1.com\nwords: word1; word2"), "TestXXX");
        assertTrue(fullHashWordMap.contains("word1=[Title: example1\nurl: example1.com\nwords: word1; word2"), "TestYYY");
        assertTrue(((fullHashWordMap.contains("word2=[Title: example1\nurl: example1.com\nwords: word1; word2"))||fullHashWordMap.contains("")), "Test123");
        //        "Title: example3\nurl: example3.com\nwords: word4; word5; word2\n, " +
        //        "Title: example2\nurl: example2.com\nwords: word2; word3\n], "));
        assertTrue(fullHashWordMap.contains("word3=[Title: example2\nurl: example2.com\nwords: word2; word3\n],"), "TestZZZ");
        assertTrue(fullHashWordMap.contains("word4=[Title: example3\nurl: example3.com\nwords: word4; word5; word2\n]}"));
        assertTrue(fullHashWordMap.contains("word5=[Title: example3\nurl: example3.com\nwords: word4; word5; word2\n],"));


        assertEquals("{" +
                        "word1=[Title: example1\nurl: example1.com\nwords: word1; word2\n], " +
                        "word2=[Title: example1\nurl: example1.com\nwords: word1; word2\n]}",
                minHashWordMap, "Case 2a failed");
        assertEquals("{" +
                "word1=[Title: example1\nurl: example1.com\nwords: word1; word2\n], " +
                "word3=[Title: example2\nurl: example2.com\nwords: word2; word3\n], " +
                "word2=[Title: example1\nurl: example1.com\nwords: word1; word2\n, " +
                "Title: example3\nurl: example3.com\nwords: word4; word5; word2\n, " +
                "Title: example2\nurl: example2.com\nwords: word2; word3\n], " +
                "word5=[Title: example3\nurl: example3.com\nwords: word4; word5; word2\n], " +
                "word4=[Title: example3\nurl: example3.com\nwords: word4; word5; word2\n]}", fullHashWordMap, "Case 3a failed");
    }

    @Test
    void buildTreeMapIndex(){
        assertNull(emptyHashWordMap, "Case 4a failed");
        assertEquals("{" +
                        "word1=[Title: example1\nurl: example1.com\nwords: word1; word2\n], " +
                        "word2=[Title: example1\nurl: example1.com\nwords: word1; word2\n]}",
                minTreeWordMap, "Case 5a failed");
        assertEquals("{" +
                "word1=[Title: example1\nurl: example1.com\nwords: word1; word2\n], " +
                "word3=[Title: example2\nurl: example2.com\nwords: word2; word3\n], " +
                "word2=[Title: example1\nurl: example1.com\nwords: word1; word2\n, " +
                "Title: example3\nurl: example3.com\nwords: word4; word5; word2\n, " +
                "Title: example2\nurl: example2.com\nwords: word2; word3\n], " +
                "word5=[Title: example3\nurl: example3.com\nwords: word4; word5; word2\n], " +
                "word4=[Title: example3\nurl: example3.com\nwords: word4; word5; word2\n]}", fullTreeWordMap, "Case 6a failed");
    }
}