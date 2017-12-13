package searchengine;

import searchengine.Indexes.Index;
import searchengine.Ranking.IRanker;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */

public class IndexMethods {

    // testing method
    public static void main(String[] args) {
        List<List<String>> result;
        String one = "The OR That, nothing OR it OR bla bla. bla OR bla bla bla";
        String two = "  two  ";
        String three = "  one two three  four";
        String four = " President USA OR Queen Danmark OR Chancellor Germany";
        String five = " or sunrise in modern in geography known as asia minor from small asia in modern OR american state ";
        String six = "OR  Anatolia   OR  Australia OR";
        String string = six;

        IndexMethods im = new IndexMethods();

        for (List<String> list : im.splitQuery(string)) {
            for (String s : list) {
                System.out.println(s);
            }
            System.out.println();
        }

        result = splitQuery(string);
        System.out.println(result);
        result = modifyQuery(result);
        System.out.println(result);
        System.out.println(modifyQuery(splitQuery(string)));
    }

    /**
     * Sergio
     * splitQuery method is processing the query line such that:
     * 1. if it's a single word - it stays as a query;
     * 2. if query contains 'OR' it splits on OR-statements
     * 3. if in every OR-statement there is " " so it splits on words and makes a group with AND-statement
     *
     * @param query is a String parameter of query
     * @return result is a List of OR-statements that are Lists of AND-statements.
     * <p>
     * So when we process the query, we iterate through OR, looking for ANY OF THEM on the site,
     * but inside each of OR-statement ALL words shall be presenting on the site.
     */
    public static List<List<String>> splitQuery(String query) {
        ArrayList<List<String>> result = new ArrayList<>();
        ArrayList<String> andWords;
        //String line = query.trim();
        String[] splitOR = new String[]{query};

        if (query.matches(".*?\\s+OR\\s+.*?")) {
            splitOR = query.split("\\s+OR\\s+");
        }
        for (String andSentence : splitOR) {
            andWords = new ArrayList<>();
            String[] temp = new String[]{andSentence};
            if (andSentence.contains(" ")) temp = andSentence.split(" ");
            for (String words : temp) {
                String word;
                if (!(word = words.trim()).isEmpty()) andWords.add(word);
            }
            if (!result.contains(andWords) && andWords.size() != 0) result.add(andWords);
        }
        return result;
    }

    /**
     * modifyQuery modifies the query, to ease use of regular expressions, by passing the elements of the query through the modifyWordList method.
     * The modifyQuery method requires that queries have already been processed by the splitQuery method.
     *
     * @param query is a List of OR-statements, that are Lists of AND-statements.
     * @return returns a List of OR-statements, that are Lists of AND-statements, that have been converted to lower-case,
     * and had punctuation and unnecessary spaces removed.
     * <p>
     * When running this method we iterate through the Lists of strings in query, passing them to the modifyWordList method.
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
     * modifyWordList modifies a list of words, to ease use of regular expressions.
     * This is done by the following:
     * 1. converting query words to lower-case
     * 2. converting any use of punctuation to single space // TODO: 24-Oct-17 This could become a problem since two words might get treated as a single word
     * 3. converting any longer spaces in queries, e.g. uses of tab or double space to single space
     * 4. removing any space after a query word
     * <p>
     * @param wordList is a list of keywords, either from a query String or from a given website.
     * @return a list of strings that have been converted to lower case and had punctuation and unnecessary spaces removed.
     *
     * When running this method, we iterate through the strings in lists of strings.
     */
    public static List<String> modifyWordList(List<String> wordList){
        List<String> tempList = new ArrayList<>();
        for (String word : wordList) {
            String modifiedWord = word.toLowerCase()
                    .replaceAll("\\p{Punct}", " ")
                    .replaceAll("\\s+", " ")
                    .replaceAll("\\s+$|^\\s+", "");
            if (!modifiedWord.isEmpty()) tempList.add(modifiedWord);    // no empty lines in word Lists
        }
        return tempList;
    }

    /**
     * This methods finds all the websites matching the AND/OR conditions of a multi-word query.
     * NOTE: This method also uses modifiesQuery on the given query
     * 28.11.17 added a check if sites contains all of AND-separated words. If not, it just jumps to next AND-separated line.
     * If at least one site contains all words, it runs normally.
     *
     * @param index          The Index that will perform the singleLookup on the search words
     * @param multiWordQuery The query to match. Each whitespace is treated as an AND condition and each " OR " is
     *                       treated as an OR condition.
     * @return A list of websites matching at least one of the OR conditions of the query.
     */
    public static List<Website> multiWordQuery(Index index, String multiWordQuery, IRanker ranker) {
        List<List<String>> splitQueries = modifyQuery(splitQuery(multiWordQuery));
        Map<Website, Float> allRanks = new HashMap<>();
        // We loop over each OR separated list of query words
        for (int i = 0; i < splitQueries.size(); i++) {
            List<String> andSeparatedSearchWords = splitQueries.get(i);
            Map<Website, Float> currentRanks = new HashMap<>();      // Ranks for the current list of AND separated words.
            String initialSearchWord = andSeparatedSearchWords.get(0);
            List<Website> lookUp = index.lookup(initialSearchWord);

            for (Website site : lookUp) {
                if (site.containsAllWords(andSeparatedSearchWords)) {                    // 28.11.17 checks all words
                    currentRanks.put(site, ranker.getScore(initialSearchWord, site, index));
                    for (int j = 1; j < andSeparatedSearchWords.size(); j++) {
                        currentRanks.merge(site, ranker.getScore(andSeparatedSearchWords.get(j), site, index), Float::sum);
                    }
                }
            }

            for (Map.Entry<Website, Float> mapEntry : currentRanks.entrySet()) {
                Website site = mapEntry.getKey();
                Float currentRank = mapEntry.getValue();
                Float newRank = Math.max(allRanks.getOrDefault(site, (float) 0), currentRank);
                allRanks.put(site, newRank);
            }
        }
        // The line below selects all the Websites to a List and sorts them according to the key (score).
        return allRanks.entrySet().stream().sorted((x, y) -> y.getValue().
                compareTo(x.getValue())).map(Map.Entry::getKey).collect(
                Collectors.toList());
    }

    /**
     * NOTE This method is identical to multiWordQuery but uses a different algorithm
     * This methods finds all the websites matching the AND/OR conditions of a multi-word query.
     * NOTE: This method also uses modifiesQuery on the given query
     *
     * @param index          The Index that will perform the singleLookup on the search words
     * @param multiWordQuery The query to match. each whitespace is treated as an AND condition and each " OR " is
     *                       treated as an OR condition
     * @return A list of websites matching at least one of the or conditions of the query.
     */
    /*public static List<Website> multiWordQuery2(Index index, String multiWordQuery, IRanker ranker) {
        List<List<String>> splitQueries = splitQuery(multiWordQuery);

        Map<Website, Float> allRanks = new HashMap<>();

        // We loop over each OR separated list of query words
        for (int i = 0; i < splitQueries.size(); i++) {
            List<String> andSeparatedSearchWords = splitQueries.get(i);
            Map<Website, Float> currentRanks = new HashMap<>(); // Ranks for the current list of AND seperated words.

            // TODO: 31-Oct-17 Improve speed by using longest (least frequent) word here
            String initialSearchWord = andSeparatedSearchWords.get(0);
            for (Website site : index.lookup(initialSearchWord)) {
                currentRanks.put(site, ranker.getScore(initialSearchWord, site, index));
            }

            // For each other searchword than the initial search words, we check the initial sites found if these
            // sites also contains the rest of the search words.
            for (int j = 1; j < andSeparatedSearchWords.size(); j++) {
                String queryWord = andSeparatedSearchWords.get(j);
                List<Website> sitesToRemove = new ArrayList<>();


                for (Map.Entry<Website, Float> mapEntry : currentRanks.entrySet()) {
                    Website site = mapEntry.getKey();
                    float currentRank = mapEntry.getValue();
                    if (site.containsWord(queryWord)) {
                        currentRanks.put(site, currentRank + ranker.getScore(queryWord, site, index));
                    } else
                        sitesToRemove.add(site);
                }
                sitesToRemove.forEach(currentRanks::remove);
            }
            for (Map.Entry<Website, Float> mapEntry : currentRanks.entrySet()) {
                Website site = mapEntry.getKey();
                Float currentRank = mapEntry.getValue();
                Float newRank = Math.max(allRanks.getOrDefault(site, (float) 0), currentRank);
                allRanks.put(site, newRank);
            }
        }
        // The line below selects a all the Websites to a List and sorts them according to the key (score).
        return allRanks.entrySet().stream().sorted((x, y) -> y.getValue().
                compareTo(x.getValue())).map(Map.Entry::getKey).collect(
                Collectors.toList());
    }*/
    public static List<Website> multiWordQuery2(Index index, String multiWordQuery, IRanker ranker) {
        // TODO: 31-Oct-17 This method can be improved by making a dictionary with all words in the query and
        // TODO: 31-Oct-17 corresponding search results so the same single query word is not looked up multiple times

        List<List<String>> splitQueries = modifyQuery(splitQuery(multiWordQuery));

        Map<Website, Float> allRanks = new HashMap<>();

        // We loop over each OR separated list of query words
        for (int i = 0; i < splitQueries.size(); i++) {
            List<String> andSeparatedSearchWords = splitQueries.get(i);
            Map<Website, Float> currentRanks = new HashMap<>(); // Ranks for the current list of AND separated words.

            // TODO: 31-Oct-17 Improve speed by using longest (least frequent) word here
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
            for (Map.Entry<Website, Float> mapEntry : currentRanks.entrySet()) {
                Website site = mapEntry.getKey();
                Float currentRank = mapEntry.getValue();
                Float newRank = Math.max(allRanks.getOrDefault(site, (float) 0), currentRank);
                allRanks.put(site, newRank);
            }
        }
        // The line below selects a all the Websites to a List and sorts them according to the key (score).
        return allRanks.entrySet().stream().sorted((x, y) -> y.getValue().
                compareTo(x.getValue())).map(Map.Entry::getKey).collect(
                Collectors.toList());
    }
}
