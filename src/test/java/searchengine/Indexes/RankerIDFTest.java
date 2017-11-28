package searchengine.Indexes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RankerIDFTest {

    List<Website> sites;
    private RankerIDF ranker;
    private Index index;
    private Index emptyIndex;

    @BeforeEach
    void setUp() {
        sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word2", "word6", "word7")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3", "word6", "word3", "word7", "word8", "word8", "word3")));
        sites.add(new Website("example3.com", "example3", Arrays.asList("word2", "word3", "word4", "word3", "word5", "word6", "word7")));
        ranker = new RankerIDF(sites);
        index = new ReverseHashMapIndex();
        index.build(sites);
        emptyIndex = new ReverseHashMapIndex();
    }


    /**
     * The getScore uses the formula: tfidf (w, S, D) = tf(w,S) * idf(w,D)
     * where
     * w is the query-word
     * S is the site containing w
     * D is the database containing the sites
     * tfidf is the score
     * <p>
     * In the following tests database consists of 3 sites => D=3
     * And n is the number of sites containing the word
     */

    @Test
    void testGetScoreValues() {
        float expectedValue;
        //Here the word occurs 1 time on site 1, on only that site
        expectedValue = 1 * (float) (Math.log(sites.size() / (float) index.lookup("word1").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.getScore("word1", sites.get(0), index), 1e-6, "getScore test failed for word1, site 1");
        //Here the word occurs 2 times on site 2, on only that site
        expectedValue = 2 * (float) (Math.log(sites.size() / (float) index.lookup("word8").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.getScore("word8", sites.get(1), index), 1e-6, "getScore test failed for word8, site 2");
        //Here the word occurs 3 times on site 2, occurs on 2 other sites
        expectedValue = 3 * (float) (Math.log(sites.size() / (float) index.lookup("word3").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.getScore("word3", sites.get(1), index), 1e-6, "getScore test failed for word3, site 2");
        //Here the word occurs 0 times on site 2, occurs on 1 other site
        expectedValue = 0 * (float) (Math.log(sites.size() / (float) index.lookup("word1").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.getScore("word1", sites.get(1), index), 1e-6, "getScore test failed for word1, site 2");
    }


    /**
     * The tf uses the formula: tf(w,S) = m
     * where
     * w is the query-word
     * S is the site containing w
     * and m is the frequency of the word on that site
     * <p>
     */

    @Test
    void testTFValues() {
        //Here the word occurs 1 time on a site => m=1
        assertEquals(1, ranker.tf("word5", sites.get(2)), "tfValues test 1 failed for word5, site 3");
        //Here the word occurs 2 times on a site => m=2
        assertEquals(2, ranker.tf("word2", sites.get(0)), "tfValues test 2 failed for word2, site 1");
        //Here the word occurs 3 times on a site => m=3
        assertEquals(3, ranker.tf("word3", sites.get(1)), "tfValues test 3 failed for word3, site 2");
    }

    @Test
    void testTFCornerValues() {
        //Here the word occurs 0 times on a site => m=0
        assertEquals(0, ranker.tf("word0", sites.get(0)), "tfCornerValues test 1 failed for word0, site 1");
    }


    /**
     * The IDF uses the formula: IDF(w,D) = log2(D/n)
     * where
     * w is the query-word
     * n is the number of sites containing w
     * D is the database containing the sites
     * <p>
     * In the following tests database consists of 3 sites => D=3
     */

    @Test
    void testIDFValues() {
        float expectedValue;
        //Here the word occurs on 3 sites, and D=3 => log2(3/3) = 0
        expectedValue = (float) (Math.log(sites.size() / (float) index.lookup("word2").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.idf("word2", index), 1e-6, "idfValues test 1 failed for word0");
        //Here the word occurs on 1 site, and D=3 => log2(3/1) = 1,6 (ish)
        expectedValue = (float) (Math.log(sites.size() / (float) index.lookup("word1").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.idf("word1", index), 1e-6, "idfValues test 2 failed for word1");
        //Here the word occurs on 2 sites, and D=3 => log2/3/2) = 0,6 (ish)
        expectedValue = (float) (Math.log(sites.size() / (float) index.lookup("word3").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.idf("word3", index), 1e-6, "idfValues test 3 failed for word3");
    }

    @Test
    void testIDFCornerValues() {
        float expectedValue = 0;
        //Here the word occurs on 0 sites, and D=3 => log2(3/0) = infinity
        assertEquals(expectedValue, ranker.idf("word0", index), 1e-6, "idfCornerValues test 1 failed for word0, non-empty index");
        //Here the word occurs on 0 sites, and D=0 => log2(0/0) = infinity
        assertEquals(expectedValue, ranker.idf("word0", emptyIndex), 1e-6, "idfCornerValues test 2 failed for word0, empty index");
    }

}