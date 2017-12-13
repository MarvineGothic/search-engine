package searchengine.Indexes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Ranking.RankerBM25;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RankerBM25Test {

    List<Website> sites;
    List<Website> emptySites;
    float totalAmountOfWordsSites = 0;
    float AVDLSites;
    float getTotalAmountOfWordsEmptySites = 0;
    float AVDLEmptySites = 0;
    private RankerBM25 ranker;
    private Index index;
    private Index emptyIndex;

    @BeforeEach
    void setUp() {
        sites = new ArrayList<>();
        sites.add(new Website("example1.com", "example1", Arrays.asList("word1", "word2", "word2", "word6", "word7")));
        sites.add(new Website("example2.com", "example2", Arrays.asList("word2", "word3", "word6", "word3", "word7", "word8", "word8", "word3")));
        sites.add(new Website("example3.com", "example3", Arrays.asList("word2", "word3", "word4", "word3", "word5", "word6", "word7")));
        ranker = new RankerBM25(sites);
        index = new ReverseHashMapIndex();
        index.build(sites);
        for (Website site : sites) {
            totalAmountOfWordsSites += site.getWords().size();
        }
        AVDLSites = totalAmountOfWordsSites / sites.size();
        emptySites = new ArrayList<>();
        emptyIndex = new ReverseHashMapIndex();
    }


    @AfterEach
    void tearDown() {
        totalAmountOfWordsSites = 0;
    }


    /**
     * The bm25 uses the formula: bm(w,S, D) = tf^(w,S,D) * idf(w,D)
     * where
     * tf^(w,S,D) = tf(w,S)*(1,75+1)/(1,75(1-0,75+0,75*DL/AVDL)+tf(w,S))
     * where
     * DL is the words on the site S
     * AVDL is the average amount of words on the sites in D
     * 1,75 and 0,75 are constants that are predetermined
     * <p>
     */


    @Test
    void testGetScoreValues() {
        float expectedValue;
        String[] words = new String[]{"word1", "word2", "word3"};
        //Here word1 on site1 is tested.
        for (Website site : sites) {
            for (String word : words) {
                float tf = ranker.tf(word, site);
                float idf = ranker.idf(word, index);
                float DL = site.getWords().size();
                //float DL = (sites.size() > 0 ? sites.size() : 1);
                expectedValue = idf * (tf * (1.75f + 1f) / (1.75f * (1f - 0.75f + 0.75f * DL / AVDLSites) + tf));
                assertEquals(expectedValue, ranker.getScore(word, site, index), 1e-6, "getScoreValues failed for " + word + " and site " + site);
            }
        }
    }

    @Test
    void testGetScoreCornerValues() {
        float expectedValue = 0;
        /*float tf = ranker.tf("word0", sites.get(0));
        float idf = ranker.idf("word0", index);
        float DL = sites.get(0).getWords().size();
        expectedValue = idf * (tf * (1.75f + 1f) / (1.75f * (1f - 0.75f + 0.75f * DL / AVDLSites) + tf));  */
        assertEquals(expectedValue, ranker.getScore("word0", sites.get(0), index), 1e-6, "getScoreValues failed for word0 on site1");

    }


}