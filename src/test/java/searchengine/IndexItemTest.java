package searchengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Indexes.IndexItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IndexItemTest {
    Website website;


    @BeforeEach
    void setUp() {
        website = new Website("www.test1.com", "site1", Arrays.asList("word1", "word2", "word3"));
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testEquality() {
        IndexItem itemA = new IndexItem(website, "word1");
        IndexItem itemB = new IndexItem(website, "word1");

        assertEquals(itemA, itemB);
        assertEquals(true, itemA.equals(itemB));
    }

    @Test
    void testContains() {
        IndexItem itemA = new IndexItem(website, "word1");
        IndexItem itemB = new IndexItem(website, "word1");
        Set<IndexItem> set = new HashSet<>();
        set.add(itemA);
        assertEquals(true, set.contains(itemB));
    }

    @Test
    void testNotEquality() {
        IndexItem itemA = new IndexItem(website, "word1");
        IndexItem itemB = new IndexItem(website, "word2");
        assertEquals(false, itemA.equals(itemB));
    }

    @Test
    void testNotContains() {
        IndexItem itemA = new IndexItem(website, "word1");
        IndexItem itemB = new IndexItem(website, "word2");
        Set<IndexItem> set = new HashSet<>();
        set.add(itemA);
        assertEquals(false, set.contains(itemB));
    }

}
