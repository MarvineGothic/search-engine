package searchengine;

import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Indexes.InvertedTreeMapIndex;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class InvertedIndexTest {
    private List<Website> minWebsiteList = null;
    private List<Website> fullWebsiteList = null;
    private Pattern word1 = Pattern.compile("\\[Title: example1\\nurl: example1.com\\nwords:( word[12];?){2}\\n]");
    private Pattern word2 = Pattern.compile("word2=\\[(Title: example[123]\\nurl: example[123].com\\nwords:( word[12345];?){2,3}+\\n?,? ?){3}\\n]");
    private Pattern word3 = Pattern.compile("\\[Title: example2\\nurl: example2.com\\nwords:( word[23];?){2}\\n]");
    private Pattern word4 = Pattern.compile("\\[Title: example3\\nurl: example3.com\\nwords:( word[245];?){3}\\n]");
    private Pattern word5 = Pattern.compile("\\[Title: example3\\nurl: example3.com\\nwords:( word[245];?){3}\\n]");

    @BeforeEach
    void setUp() {
        minWebsiteList = new ArrayList<>();
        fullWebsiteList = new ArrayList<>();
        minWebsiteList.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2")));
        fullWebsiteList.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2")));
        fullWebsiteList.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3")));
        fullWebsiteList.add(new Website("example3.com","example3", Arrays.asList("word4","word5","word2")));
    }

    @AfterEach
    void tearDown() {
        minWebsiteList = null;
        fullWebsiteList = null;
    }

    /**
     * <pre>
     * Tests the build method of InvertedHashMapIndex.
     *
     * Case 1a-3a: Testing build method for InvertedHashMapIndex
     * Case 1a: Testing if build method crashes if it is called on an empty websiteList
     * Case 2a: Testing if build method works for websiteList containing 1 website
     * Case 3a: Testing if build method works for websiteList containing 3 websites
     *
     * Case 4a-6a: Testing build method for InvertedTreeMapIndex
     * Case 4a: Testing if build method crashes if it is called on an empty websiteList
     * Case 5a: Testing if build method works for websiteList containing 1 website
     * case 6a: Testing if build method works for websiteList containing 3 websites
     * </pre>
     */
    @Test
    void buildInvertedIndex(){
        buildHashMapIndex();
        buildTreeMapIndex();
    }

    private void buildHashMapIndex() {
        //Case 1a - Empty websiteList
        Map<String, HashSet<IndexedWebsite>> emptyHashWordMap = new InvertedHashMapIndex().getWordMap();
        assertNull(emptyHashWordMap, "Case 1a failed, actual output was: "+emptyHashWordMap);

        //Case 2a - Single website in websiteList
        InvertedHashMapIndex minHashMapIndex = new InvertedHashMapIndex();
        minHashMapIndex.build(minWebsiteList);
        String minHashWordMap = minHashMapIndex.getWordMap().toString();
        Matcher matcher1 = word1.matcher(minHashWordMap);
        assertTrue(matcher1.find(), "Case 2a failed, actual output was: "+minHashWordMap);

        //Case 3a - 3 websites in websiteList
        InvertedHashMapIndex fullHashMapIndex = new InvertedHashMapIndex();
        fullHashMapIndex.build(fullWebsiteList);
        String fullHashWordMap = fullHashMapIndex.getWordMap().toString();
        assertTrue(word1.matcher(fullHashWordMap).find(), "Case 3a failed at word 1, actual output was: "+fullHashWordMap);
        assertTrue(word2.matcher(fullHashWordMap).find(), "Case 3a failed at word 2, actual output was: "+fullHashWordMap);
        assertTrue(word3.matcher(fullHashWordMap).find(), "Case 3a failed at word 3, actual output was: "+fullHashWordMap);
        assertTrue(word4.matcher(fullHashWordMap).find(), "Case 3a failed at word 4, actual output was: "+fullHashWordMap);
        assertTrue(word5.matcher(fullHashWordMap).find(), "Case 3a failed at word 5, actual output was: "+fullHashWordMap);
    }

    private void buildTreeMapIndex(){
        //Case 4a - Empty websiteList
        Map<String, HashSet<IndexedWebsite>> emptyTreeWordMap = new InvertedTreeMapIndex().getWordMap();
        assertNull(emptyTreeWordMap, "Case 4a failed, actual output was: "+emptyTreeWordMap);

        //Case 5a - Single website in websiteList
        InvertedTreeMapIndex minTreeMapIndex = new InvertedTreeMapIndex();
        minTreeMapIndex.build(minWebsiteList);
        String minTreeWordMap = minTreeMapIndex.getWordMap().toString();
        Matcher matcher1 = word1.matcher(minTreeWordMap);
        assertTrue(matcher1.find(), "Case 5a failed, actual output was: "+minTreeWordMap);

        //Case 6a - 3 websites in websiteList
        InvertedTreeMapIndex fullTreeMapIndex = new InvertedTreeMapIndex();
        fullTreeMapIndex.build(fullWebsiteList);
        String fullTreeWordMap = fullTreeMapIndex.getWordMap().toString();
        assertTrue(word1.matcher(fullTreeWordMap).find(), "Case 6a failed at word 1");
        assertTrue(word2.matcher(fullTreeWordMap).find(), "Case 6a failed at word 2");
        assertTrue(word3.matcher(fullTreeWordMap).find(), "Case 6a failed at word 3");
        assertTrue(word4.matcher(fullTreeWordMap).find(), "Case 6a failed at word 4");
        assertTrue(word5.matcher(fullTreeWordMap).find(), "Case 6a failed at word 5");
    }
}