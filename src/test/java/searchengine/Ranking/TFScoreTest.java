package searchengine.Ranking;

import org.junit.jupiter.api.Test;
import searchengine.Ranking.TFScore;
import searchengine.Website;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The tf uses the formula: tf(w,S) = m
 * where
 * w is the query-word
 * S is the site containing w
 * and m is the frequency of the word on that site
 */
public class TFScoreTest {
    private Website site = new Website("www.example1.com", "example1", Arrays.asList("a", "b", "b", "c", "c", "c"));

    @Test
    void castTest(){

        boolean error = false;
        try {
            Float f = null;
            float f2 = f;
            error = false;
        } catch (Exception e){
            error = true;

        }
        assertEquals(false, error);
    }

    @Test
    void testTFValues() {
        //Here the word occurs 1 time on a site => m=1
        assertEquals(1, TFScore.tf("a", site), "tfValues test 1 failed for a");
        //Here the word occurs 2 times on a site => m=2
        assertEquals(2, TFScore.tf("b", site), "tfValues test 2 failed for b");
        //Here the word occurs 3 times on a site => m=3
        assertEquals(3, TFScore.tf("c", site), "tfValues test 3 failed for c");
    }

    @Test
    void testTFCornerValues() {
        //Here the word occurs 0 times on a site => m=0
        assertEquals(0, TFScore.tf("wordNotInSite", site), "tfCornerValues test 1 failed for wordNotInSite");
    }
}
