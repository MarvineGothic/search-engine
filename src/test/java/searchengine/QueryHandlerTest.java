package searchengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Ranking.BM25Score;
import searchengine.Ranking.Score;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static searchengine.QueryHandler.multiWordQuery;
import static searchengine.QueryHandler.splitQuery;
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
    private Score ranker;

    private Website one = new Website("1.com", "example1", Collections.singletonList("a"));
    private Website two = new Website("2.com", "example2", Arrays.asList("a", "b"));
    private Website three = new Website("3.com", "example3", Arrays.asList("b", "c"));
    private Website four = new Website("4.com", "example4", Arrays.asList("b", "c", "d"));
    private Website five = new Website("5.com", "example5", Collections.singletonList("e"));
    private Website six = new Website("6.com", "example6", Collections.singletonList("f"));

    @BeforeEach
    void setUp() {
        List<Website> sites = new ArrayList<>();
        sites.add(one);
        sites.add(two);
        sites.add(three);
        sites.add(four);
        sites.add(five);
        sites.add(six);

        index = new InvertedHashMapIndex();
        index.build(sites);

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
        //Expected sites based on the words "b c" --> order of site: three then four
        expectedSites.add(three);
        expectedSites.add(four);
        assertEquals(expectedSites.toString(), multiWordQuery(index, "b c", ranker).toString());
    }

    @Test
    void testModifyQueryMethod(){
        assertEquals("denmark", QueryHandler.modifyQuery(splitQuery("Denmark OR Germany")).get(0).get(0));
        assertEquals("denmark", QueryHandler.modifyQuery(splitQuery("denmark OR Germany")).get(0).get(0));

        // spaces and punctuation
        assertEquals(1, QueryHandler.modifyQuery(splitQuery(" word1,")).size());
        assertEquals("word1", QueryHandler.modifyQuery(splitQuery(" word1,")).get(0).get(0));
        assertEquals(0, QueryHandler.modifyQuery(splitQuery(" ")).size());
        assertEquals("[]", QueryHandler.modifyQuery(splitQuery(" ")).toString());
        assertEquals("[]", QueryHandler.modifyQuery(splitQuery(". ")).toString());
        assertEquals(0, QueryHandler.modifyQuery(splitQuery(". ")).size());
    }
}