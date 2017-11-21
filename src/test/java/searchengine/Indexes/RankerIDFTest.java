package searchengine.Indexes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RankerIDFTest {

    private RankerIDF ranker;
    private Index index;
    List<Website> sites;

    @BeforeEach
    void setUp() {
        sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word6", "word7")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3", "word6", "word7", "word8")));
        sites.add(new Website("example3.com", "example3", Arrays.asList("word2", "word3", "word4", "word5", "word6", "word7")));
        ranker = new RankerIDF(sites);
        index = new ReverseHashMapIndex();
        index.build(sites);
    }


    @AfterEach
    void tearDown() {
    }

    /**
     * Tests the ranker methods with ReverseHashMapIndex.
     */

    @Test
    void getScore() {
    }

    @Test
    void tf() {
    }


    /**
     * The IDF uses the formula: IDF(w,D) = log2(D/n)
     * where
     * w is the query-word
     * n is the number of sites containing w
     * and D is the database as a whole (all sites)
     * <p>
     * in the following tests database consists of 3 sites => D=3
     */

    @Test
    void idf() {
        //Here the word occurs on 3 sites, and D=3 => log2(3/3) = 0
        assertEquals(Math.log(sites.size() / index.lookup("word2").size()) / Math.log(2), ranker.idf("word2", index), 1e-6,"idf test 1 failed (word2)");
        //Here the word occurs on 1 site, and D=3 => log2(3/1) = 1,6 (ish)
        assertEquals(Math.log(sites.size() / index.lookup("word1").size()) / Math.log(2), ranker.idf("word1", index), 1e-6,"idf test 2 failed (word1)");

    }

}