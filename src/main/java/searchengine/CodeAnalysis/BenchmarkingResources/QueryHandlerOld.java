package searchengine.CodeAnalysis.BenchmarkingResources;

import searchengine.Indexes.Index;
import searchengine.QueryHandler;
import searchengine.Ranking.Score;
import searchengine.Website;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * NOTE This class is identical to IndexMethod except for a single method and is only used for benchmarking
 * </pre>
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public class QueryHandlerOld extends QueryHandler {
    /**
     * <pre>
     * NOTE This method is identical to multiWordQuery from the parent class but uses a different algorithm. It is
     * currently only used for benchmarking.
     *
     * This methods finds all the websites matching the AND/OR conditions of a multi-word query.
     * NOTE: This method also uses modifiesQuery on the given query
     *
     * @param index          The Index that will perform the singleLookup on the search words
     * @param multiWordQuery The query to match. Each whitespace is treated as an AND condition and each " OR " is
     *                       treated as an OR condition.
     * @param ranker         A ranker used to sort the found websites
     * @return A list of websites matching at least one of the OR conditions of the query.
     * </pre>
     */
    public static List<Website> multiWordQuery(Index index, String multiWordQuery, Score ranker) {
        List<List<String>> splitQueries = modifyQuery(splitQuery(multiWordQuery));

        Map<Website, Float> allRanks = new HashMap<>();

        // We loop over each OR separated list of query words
        for (List<String> andSeparatedSearchWords : splitQueries) {
            Map<Website, Float> currentRanks = new HashMap<>(); // Ranks for the current list of AND separated words.

            String initialSearchWord = andSeparatedSearchWords.get(0);
            for (Website site : index.lookup(initialSearchWord)) {
                currentRanks.put(site, ranker.getScore(initialSearchWord, site, index));
            }

            // For each other search word than the initial search words, we check the initial sites found if these
            // sites also contains the rest of the search words.
            for (int j = 1; j < andSeparatedSearchWords.size(); j++) {
                String queryWord = andSeparatedSearchWords.get(j);
                List<Website> currentSites = index.lookup(queryWord);
                Set<Website> currentKeys = new HashSet<>(currentRanks.keySet());
                for (Website site : currentKeys) {
                    if (currentSites.contains(site)) {
                        Float currentRank = currentRanks.get(site);
                        if (currentRank != null) // In case the
                            currentRanks.put(site, currentRank + ranker.getScore(queryWord, site, index));
                    } else
                        currentRanks.remove(site);
                }
            }
            updateAllRanks(currentRanks, allRanks);
        }
        // The line below selects a all the Websites to a List and sorts them according to the key (score).
        return allRanks.entrySet().stream().sorted((x, y) -> y.getValue().
                compareTo(x.getValue())).map(Map.Entry::getKey).collect(
                Collectors.toList());
    }

}
