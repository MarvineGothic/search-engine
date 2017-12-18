package searchengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.CodeAnalysis.BenchmarkingResources.IndexMethodsOld;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Indexes.SimpleIndex;

import searchengine.Ranking.BM25Score;
import searchengine.Ranking.Score;
import searchengine.Ranking.SimpleScore;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static searchengine.IndexMethods.multiWordQuery;
//import static searchengine.CodeAnalysis.BenchmarkingResources.IndexMethodsOld.multiWordQuery;


/**
 * <pre>
 * The QueryHandlerTest class tests the different outputs from the multiWordQuery method based on different inputs.
 *
 * If you want to test the older version from the deprecated class "IndexMethodsOld" simply comment out the import
 * statement above "import static searchengine.IndexMethods.multiWordQuery;" and instead use the import statement
 * "import static searchengine.CodeAnalysis.BenchmarkingResources.IndexMethodsOld.multiWordQuery;"
 * </pre>
 */

class QueryHandlerTest {
    private Index index;
    private Index simpleindex;


    private Website one = new Website("1.com", "example1", Collections.singletonList("a"));
    private Website two = new Website("2.com", "example2", Arrays.asList("a", "b"));
    private Website three = new Website("3.com", "example3", Arrays.asList("b", "c"));
    private Website four = new Website("4.com", "example4", Arrays.asList("b", "c", "d"));
    private Website five = new Website("5.com", "example5", Collections.singletonList("e"));
    private Website six = new Website("6.com", "example6", Collections.singletonList("f"));

    private Score ranker;
    private Score simpleRanker;


    @BeforeEach
    void setUp() {
        List<Website> sites = new ArrayList<>();
        sites.add(one);
        sites.add(two);
        sites.add(three);
        sites.add(four);
        sites.add(five);
        sites.add(six);

        simpleindex = new SimpleIndex();
        simpleindex.build(sites);

        index = new InvertedHashMapIndex();
        index.build(sites);

        simpleRanker = new SimpleScore();
        ranker = new BM25Score(sites);
    }

    @Test
    void testActualSitesFound() {
        //Adding the sites with "a" in them
        HashSet<Website> expectedSites = new HashSet<>();
        expectedSites.add(one);
        expectedSites.add(two);
        //Adding the result from the multiWordQuery on "a" to a new HashSet
        HashSet<Website> actualSites = new HashSet<>(multiWordQuery(index, "a", ranker));
        //comparing the two HashSets
        assertEquals(expectedSites, actualSites);
    }

    @Test
    void testSingleWord() {
        //One site has "d" in it
        assertEquals(1, multiWordQuery(index, "d", ranker).size());
        //Two sites have "a" in them
        assertEquals(2, multiWordQuery(index, "a", ranker).size());
        //Three sites have "b" in them
        assertEquals(3, multiWordQuery(index, "b", ranker).size());
    }

    @Test
    void testMultipleWords() {
        //Two words
        assertEquals(1, multiWordQuery(index, "a b", ranker).size());
        //Two words reversed order
        assertEquals(1, multiWordQuery(index, "b a", ranker).size());
        //More than one website match
        assertEquals(2, multiWordQuery(index, "b c", ranker).size());
        //No website has both
        assertEquals(0, multiWordQuery(index, "a d", ranker).size());
        //three word search
        assertEquals(1, multiWordQuery(index, "b c d", ranker).size());
        //Repeated word
        assertEquals(2, multiWordQuery(index, "a a a", ranker).size());
    }

    @Test
    void testSingleORQueries() {
        //Two sites have "a" and two sites have "c"
        assertEquals(4, multiWordQuery(index, "a OR c", ranker).size());
        //two sites have "a" and one site has "d"
        assertEquals(3, multiWordQuery(index, "a OR d", ranker).size());
        //Are sites counted twice? Two sites have "a", three have "b" where one has "a" and "b"
        assertEquals(4, multiWordQuery(index, "a OR b", ranker).size());
        // Corner case: Does code remove duplicates?
        assertEquals(2, multiWordQuery(index, "a OR a", ranker).size());
    }

    @Test
    void testMultipleORQueries() {
        //One site has "d", one has "e" and one has "f"
        assertEquals(3, multiWordQuery(index, "d OR e OR f", ranker).size());
        //What if OR is followed by white space?
        assertEquals(4, multiWordQuery(index, "a OR b OR ", ranker).size());
        //What if OR is followed by a non-existing word?
        assertEquals(4, multiWordQuery(index, "a OR b OR Tiger", ranker).size());
        // Corner case: Does code remove duplicates?
        assertEquals(2, multiWordQuery(index, "a OR a OR a", ranker).size());
    }


    /**
     * <pre>
     * Test that multiWordLookup1 has the same results as multiWordLookup2
     * </pre>
     */
    // TODO: 18/12/17 Should this be included?
    @Test
    void testMultiWordLookupOneVsTwo() {
        String[] lookupQueries = new String[]{
                "word1 OR word2 OR word word3",
                "word2 OR word3",
                "word1 OR word4",
                "word1 OR word1",
                "word2 OR word3 OR word5 OR word6",
                "word1 OR word4 OR ",
                "word1 OR word1 OR OR word1",
                " word1,",
                " ",
                ". ",
                "word1 OR ",
                "Denmark OR Germany",
                "word1 OR word8 OR wordX",
                "w1 w2 w3 w12 OR w3 w0 w OR w4 w5 w6",
                "w1 w2 w3 w12 OR w3 w0 w",
                "w1 w2 w3 w12",
                "w12 w2 w3 w1",
                "w w0 w3",
                "w6 w5 w5",
        };
        for (String lookupQuery : lookupQueries) {
            List<Website> expected = IndexMethods.multiWordQuery(index, lookupQuery, ranker);
            List<Website> actual = IndexMethodsOld.multiWordQuery(index, lookupQuery, ranker);
            assertEquals(expected, actual, "Failed test for query: " + lookupQuery);
        }
    }

    @Test
    void testMultiWordQueryRankingOrderWithOR() {
        List<Website> expectedSites = new ArrayList<>();
        //Expected sites with "a OR b" -> order of sites: one, two, three then four. (Due to document length difference)
        expectedSites.add(one);
        expectedSites.add(two);
        expectedSites.add(three);
        expectedSites.add(four);
        assertEquals(expectedSites.toString(), multiWordQuery(index, "a OR b", ranker).toString());

        //Expected sites based on the words "b OR c" -> order of sites: three, four then two.
        expectedSites = new ArrayList<>();
        expectedSites.add(three);
        expectedSites.add(four);
        expectedSites.add(two);
        assertEquals(expectedSites.toString(), multiWordQuery(index, "b OR c", ranker).toString());
    }

    @Test
    void testMultiWordQueryRankingOrderWithAND() {
        List<Website> expectedSites = new ArrayList<>();

        //Expected sites based on the words "a b --> order of site: two
        expectedSites.add(two);
        assertEquals(expectedSites.toString(), multiWordQuery(index, "a b", ranker).toString());

        //Expected sites based on the words "b c" --> order of site: three then four
        expectedSites = new ArrayList<>();
        expectedSites.add(three);
        expectedSites.add(four);
        assertEquals(expectedSites.toString(), multiWordQuery(index, "b c", ranker).toString());
    }
}