package searchengine.Ranking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Website;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The bm25 uses the formula: bm(w,S, D) = tf^(w,S,D) * idf(w,D)
 * where
 * tf^(w,S,D) = tf(w,S)*(1,75+1)/(1,75(1-0,75+0,75*DL/AVDL)+tf(w,S))
 * where
 * DL is the words on the site S
 * AVDL is the average amount of words on the sites in D
 * 1,75 and 0,75 are constants that are predetermined
 */
class BM25ScoreTest {

    private List<Website> sites;
    private float AVDLSites;
    private BM25Score ranker;
    private Index index;

    @BeforeEach
    void setUp() {
        sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("a", "b", "c")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("a", "b", "b", "c", "c", "c")));
        sites.add(new Website("example3.com", "example3", Arrays.asList("a", "b", "c", "d")));
        ranker = new BM25Score(sites);
        index = new InvertedHashMapIndex();
        index.build(sites);
        float totalAmountOfWordsSites = 0;
        for (Website site : sites) {
            totalAmountOfWordsSites += site.getWords().size();
        }
        AVDLSites = totalAmountOfWordsSites / sites.size();
    }

    @Test
    void testGetScoreValues() {
        float expectedValue;
        String[] words = new String[]{"a", "b", "c"};

        for (Website site : sites) {
            for (String word : words) {
                float tf = TFScore.tf(word, site);
                float idf = ranker.idf(word, index, site);
                float DL = site.getWords().size();
                expectedValue = idf * (tf * (1.75f + 1f) / (1.75f * (1f - 0.75f + 0.75f * DL / AVDLSites) + tf));
                assertEquals(expectedValue, ranker.getScore(word, site, index), 1e-6, "getScoreValues failed for " + word + " and site " + site);
            }
        }
    }

    @Test
    void testGetScoreCornerValues() {
        assertEquals(0, ranker.getScore("word0", sites.get(0), index), 1e-6, "getScoreValues failed for word0 on site1");
    }


}