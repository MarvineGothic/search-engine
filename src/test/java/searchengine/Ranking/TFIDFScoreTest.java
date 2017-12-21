package searchengine.Ranking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The IDF uses the formula: IDF(w,D) = log2(D/n)
 * where
 * w is the query-word
 * n is the number of sites containing w
 * D is the database containing the sites
 */
class TFIDFScoreTest {
    private final Website emptySite = new Website("example0.com", "example0", new ArrayList<>());
    private final Website site1 = new Website("example1.com", "example1", Collections.singletonList("a"));
    private final Website site2 = new Website("example1.com", "example1", Arrays.asList("a", "b", "b"));
    private final Website site3 = new Website("example1.com", "example1", Arrays.asList("a", "b", "b", "c", "c", "c"));
    private List<Website> sites;
    private TFIDFScore ranker;

    @BeforeEach
    void setUp() {
        sites = new ArrayList<>();
        sites.add(emptySite);
        sites.add(site1);
        sites.add(site2);
        sites.add(site3);
        ranker = new TFIDFScore(sites);
    }

    /**
     * For IDF website doesn't do anything unless is a IndexedWebsite. Hence the use of the empty website.
     */
    @Test
    void testTFIDFValues() {
        Index index = new InvertedHashMapIndex();
        index.build(sites);

        //Here the word occurs on 1 site, occurs three times on that site, and D=4 => 3 * log2(4/1) = 6
        Website site3 = new Website("example1.com", "example1", Arrays.asList("a", "b", "b", "c", "c", "c"));
        assertEquals(6, ranker.getScore("c", site3, index), 1e-6, "idfValues test 2 failed for word1");

        //Here the word occurs on 2 sites, occurs twice on that site, and D=2 => 2 * log2(4/2) = 2
        Website site2 = new Website("example1.com", "example1", Arrays.asList("a", "b", "b"));
        assertEquals(2, ranker.getScore("b", site2, index), 1e-6, "idfValues test 1 failed for word0");

        //Here the word occurs on 3 sites, occurs once on that site and D=4 =>1 * log2(4/3) = 0.415 (ish)
        Website site1 = new Website("example1.com", "example1", Collections.singletonList("a"));
        float expectedValue = 1 * (float) (Math.log(sites.size() / (float) index.lookup("a").size()) / Math.log(2));
        assertEquals(expectedValue, ranker.getScore("a", site1, index), 1e-6, "idfValues test 3 failed for word3");
    }

    /**
     * For IDF website doesn't do anything unless is a IndexedWebsite. Hence the use of the empty website.
     */
    @Test
    void testIDFCornerValues() {
        Index emptyIndex = new InvertedHashMapIndex();
        emptyIndex.build(new ArrayList<>());

        Index index = new InvertedHashMapIndex();
        index.build(sites);

        //Here the word occurs on 0 sites, occurs 0 times on that site and D=1 => log2(1/0) = 0 * infinity (which becomes 0)
        assertEquals(0, ranker.getScore("wordNotInIndex", emptySite, index), 1e-6, "idfCornerValues test 1 failed for word0, non-empty index");

        //Here the word occurs on 0 sites, occurs 0 times on that site and D=0 => log2(0/0) = 0 * infinity (which becomes 0)
        assertEquals(0, ranker.getScore("wordNotInIndex", emptySite, emptyIndex), 1e-6, "idfCornerValues test 2 failed for word0, empty index");
    }
}
