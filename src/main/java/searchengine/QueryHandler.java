package searchengine;

import searchengine.Indexes.Index;
import searchengine.Ranking.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * This class contains a methods that are useful for all indexes.
 * </pre>
 */
public class QueryHandler {

    /**
     * <pre>
     * splitQuery method is processing the query line such that:
     * 1. if it contains punctuation it replaces them with a whitespace.
     * 2. if it's a single word - it stays as a query;
     * 3. if query contains 'OR' it splits on OR-statements
     *
     * @param query is a String parameter of query
     * @return result is a List of OR-statements that are Lists of AND-statements.
     *
     * So when we process the query, we iterate through OR, looking for ANY OF THEM on the site,
     * but inside each of OR-statement ALL words shall be presenting on the site.
     * </pre>
     */
    public static List<List<String>> splitQuery(String query) {
        ArrayList<List<String>> result = new ArrayList<>();
        ArrayList<String> andWords;
        String[] splitOR = new String[]{query};
        query = query.replaceAll("\\p{Punct}", " ");

        if (query.matches(".*?\\s+OR\\s+.*?")) {
            splitOR = query.split("\\s+OR\\s+");
        }
        for (String andSentence : splitOR) {
            andWords = new ArrayList<>();
            String[] temp = new String[]{andSentence};
            if (andSentence.contains(" "))
                temp = andSentence.split("\\s+");
            for (String words : temp) {
                String word;
                if (!(word = words.trim()).isEmpty()) andWords.add(word);
            }
            if (!result.contains(andWords) && andWords.size() != 0) result.add(andWords);
        }
        return result;
    }

    /**
     * <pre>
     * modifyQuery modifies the query, to ease use of regular expressions, by passing the elements of the query through the modifyWordList method.
     * The modifyQuery method requires that queries have already been processed by the splitQuery method.
     *
     * @param query is a List of OR-statements, that are Lists of AND-statements.
     * @return returns a List of OR-statements, that are Lists of AND-statements, that have been converted to lower-case,
     * and had punctuation and unnecessary spaces removed.
     *
     * When running this method we iterate through the Lists of strings in query, passing them to the modifyWordList method.
     * </pre>
     */
    public static List<List<String>> modifyQuery(List<List<String>> query) {
        List<List<String>> tempQuery = new ArrayList<>();
        for (List<String> wordList : query) {
            List<String> tempWordList = modifyWordList(wordList);
            if (!tempWordList.isEmpty()) tempQuery.add(tempWordList);       // no empty lines in query Lists
        }
        return tempQuery;
    }

    /**
     * <pre>
     * modifyWordList modifies a list of words, to ease use of regular expressions.
     * This is done by the following:
     * 1. converting query words to lower-case
     * 2. converting any use of punctuation to single space.
     * 3. converting any longer spaces in queries, e.g. uses of tab or double space to single space
     * 4. removing any space after a query word
     *
     * @param wordList is a list of keywords, either from a query String or from a given website.
     * @return a list of strings that have been converted to lower case and had punctuation and unnecessary spaces removed.
     *
     * When running this method, we iterate through the strings in lists of strings.
     * </pre>
     */
    public static List<String> modifyWordList(List<String> wordList) {
        List<String> tempList = new ArrayList<>();
        for (String word : wordList) {
            String modifiedWord = word.toLowerCase()
                    .replaceAll("\\p{Punct}", " ")
                    .trim();
            if (!modifiedWord.isEmpty()) tempList.add(modifiedWord);    // no empty lines in word Lists
        }
        return tempList;
    }

    /**
     * <pre>
     * This methods finds all the websites matching the AND/OR conditions of a multi-word query.
     * NOTE: This method also uses modifiesQuery on the given query
     * 28.11.17 added a check if sites contains all of AND-separated words. If not, it just jumps to next AND-separated line.
     * If at least one site contains all words, it runs normally.
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
            Map<Website, Float> currentRanks = new HashMap<>();      // Ranks for the current list of AND separated words.
            String initialSearchWord = andSeparatedSearchWords.get(0);
            List<Website> lookUp = index.lookup(initialSearchWord);

            for (Website site : lookUp) {
                if (site.containsAllWords(andSeparatedSearchWords)) {         // checks all words
                    currentRanks.put(site, ranker.getScore(initialSearchWord, site, index));
                    for (int j = 1; j < andSeparatedSearchWords.size(); j++) {
                        currentRanks.merge(site, ranker.getScore(andSeparatedSearchWords.get(j), site, index), Float::sum);
                    }
                }
            }

            updateAllRanks(currentRanks, allRanks);
        }
        // The line below selects all the Websites to a List and sorts them according to the value (rank).
        return allRanks.entrySet().stream().sorted((x, y) -> y.getValue().
                compareTo(x.getValue())).map(Map.Entry::getKey).collect(
                Collectors.toList());
    }

    /**
     * <pre>
     * This method takes a map of websites and ranks and join it with another map of same type.
     * In case of a key existing in both maps the maximum rank will be used.
     * @param currentRanks The new values. This map will not be changed.
     * @param allRanks The original map. This map will be updated with entries from both maps.
     * </pre>
     */
    protected static void updateAllRanks(Map<Website, Float> currentRanks, Map<Website, Float> allRanks) {
        for (Map.Entry<Website, Float> mapEntry : currentRanks.entrySet()) {
            Website site = mapEntry.getKey();
            Float currentRank = mapEntry.getValue();
            Float newRank = Math.max(allRanks.getOrDefault(site, (float) 0), currentRank);
            allRanks.put(site, newRank);
        }
    }
}
