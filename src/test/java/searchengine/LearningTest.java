package searchengine;


import searchengine.IndexMethods;
import searchengine.Indexes.*;
import searchengine.Ranking.IRanker;
import searchengine.Ranking.NoRanker;
import searchengine.Website;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * These test are not real test for the project per say, but are used to deppen our understanding of the Java
 * programming language.
 */
public class LearningTest {

    @Test
    void ClassComparisonTest(){
        Website website = new Website("www.site.com", "TestSite", new ArrayList<>());
        IndexedWebsite indexItem = new IndexedWebsite(website, "word");
        assertTrue(Website.class == website.getClass());
        assertTrue(Website.class.equals(website.getClass()));
        assertFalse(Website.class.equals(indexItem.getClass()));
//        assertFalse(Website.class.equals(null));
    }


}
