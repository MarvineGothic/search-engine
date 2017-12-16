package searchengine.Indexes;

import searchengine.IndexedWebsite;
import searchengine.PreScoredWebsite;
import searchengine.Ranking.Score;
import searchengine.Website;
import sun.reflect.generics.scope.Scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * <pre>
 * Implements the Index interface using a reverse index method
 * </pre>
 */
abstract public class InvertedIndex implements Index {
    protected Map<String, HashSet<PreScoredWebsite>> wordMap;

    /**
     * <pre>
     * This method assigns the wordMap, leaving the choice of using either a HashMap, TreeMap or any other map to
     * the implementation class.
     * </pre>
     */
    protected abstract void InitializeWordMap();

    @Override
    public void build(List<Website> websiteList) {
        InitializeWordMap();
        for (Website currentSite : websiteList) {
            for (String indexWord : currentSite.getSetOfWords()) {
                wordMap.compute(indexWord, (key, value) -> {
                    if (value == null) {
                        value = new HashSet<>();
                    }
                    value.add(new PreScoredWebsite(currentSite, indexWord));
                    return value;
                });
            }
        }
    }

    /**
     * <pre>
     * For each word, this method assigns all the the number of websites containing that word to each IndexedWebsites
     * </pre>
     */
    public void preCalculateScores(Score score){
        for (Map.Entry<String, HashSet<PreScoredWebsite>> entry : wordMap.entrySet()) {
            for (PreScoredWebsite website : entry.getValue()) {
                float f = score.getScore(entry.getKey(), website, this);
                website.setScore(f);
            }
        }
    }

    @Override
    public List<Website> lookup(String queryWord) {
        return new ArrayList<>(wordMap.getOrDefault(queryWord, new HashSet<>()));
    }

    /**
     * <pre>
     * Method used for test purposes to compare expected and actual wordMap results
     * </pre>
     */
    @Override
    public String toString() {
        return "Mapped values=" + wordMap;
    }


    /**
     * <pre>
     * @return the mapping of queryWords to websites.
     * </pre>
     */
    public Map<String, HashSet<PreScoredWebsite>> getWordMap() {
        return wordMap;
    }
}
