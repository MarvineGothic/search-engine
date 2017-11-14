package Main;

import Main.Indexes.IRanker;
import Main.Indexes.Index;

import javax.jws.WebService;
import java.util.*;

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
     * modifyQuery modifies the query, to ease use of regular expressions. This is done by the following:
     * 1. converting query words to lower-case
     * 2. converting any use of punctuation to single space // TODO: 24-Oct-17 This could become a problem since two words might get treated as a single word
     * 3. converting any longer spaces in queries, e.g. uses of tab or double space to single space
     * 4. removing any space after a query word
     * <p>
     * The modifyQuery method requires that queries have already been processed by the splitQuery method.
     *
     * @param query is a List of OR-statements, that are Lists of AND-statements.
     * @return returns a List of OR-statements, that are Lists of AND-statements, that have been converted to lower-case.
     * <p>
     * When running this method we iterate through the Lists of strings in query, and within that iteration, we iterate through the strings
     * in those lists.
     */

    public static List<List<String>> modifyQuery(List<List<String>> query) {
        List<List<String>> tempQuery = new ArrayList<>();
        for (List<String> subList : query) {

            List<String> tempSubList = new ArrayList<>();
            for (String word : subList) {
                String modifiedWord;
                modifiedWord = word.toLowerCase()
                        .replaceAll("\\p{Punct}", " ")
                        .replaceAll("\\s+", " ")
                        .replaceAll("\\s+$|^\\s+", "");
                tempSubList.add(modifiedWord);
            }
            tempQuery.add(tempSubList);
        }
        return tempQuery;
    }

    /**
     * This methods finds all the websites matching the AND/OR conditions of a multi-word query.
     * NOTE: This method also uses modifiesQuery on the given query
     *
     * @param index          The Index that will perform the singleLookup on the search words
     * @param multiWordWuery The query to match. each whitespace is treated as an AND condition and each " OR " is
     *                       treated as an OR condition.
     * @return A list of websites matching at least one of the or conditions of the query.
     */
    public static List<Website> multiWordQuery(Index index, String multiWordWuery, IRanker ranker) {
        List<List<String>> splitQueries = modifyQuery(splitQuery(multiWordWuery));

//        Set<Website> searchResults = new HashSet<>(); // This is the all the sites matching the full query
        Map<Website, Long> allRanks = new HashMap<>();
        // We loop over each OR separated list of query words
        for (int i = 0; i < splitQueries.size(); i++) {
            List<String> andSeperatedSearchWords = splitQueries.get(i);

            // TODO: 31-Oct-17 Improve speed by using longest (least frequent) word here
            
            Map<Website, Long> currentRanks = new HashMap<>(); // Ranks for the current list of AND seperated words.
            for (Website site : index.lookup(andSeperatedSearchWords.get(0))){
                currentRanks.put(site, ranker.getScore(andSeperatedSearchWords.get(0), site, index));
            }
            for (int j = 1; j < andSeperatedSearchWords.size(); j++) {
                String queryWord = andSeperatedSearchWords.get(j);
                List<Website> sitesToRemove = new ArrayList<>();
                for (Map.Entry<Website, Long> mapEntry : currentRanks.entrySet()){
                    Website site = mapEntry.getKey();
                    long currentRank = mapEntry.getValue();
                    if (site.containsWord(queryWord)) {
                        currentRanks.put(site, currentRank + ranker.getScore(queryWord, site, index));
                    } else
                        sitesToRemove.add(site);
                }
                sitesToRemove.forEach(currentRanks::remove);
            }
            for (Map.Entry<Website, Long> mapEntry : currentRanks.entrySet()) {
                Website site = mapEntry.getKey();
                long currentRank = mapEntry.getValue();
                long newRank = Math.max(allRanks.getOrDefault(site, (long) 0), currentRank);
                allRanks.put(site, newRank);
            }
        }
//        Set<Map.Entry<Website, Long>> as = allRanks.entrySet();
        int a = Arrays.asList(allRanks.);

    }

    /**
     * NOTE This method is identical to multiWordQuery but uses a different algorithm
     * This methods finds all the websites matching the AND/OR conditions of a multi-word query.
     * NOTE: This method also uses modifiesQuery on the given query
     * @param index The Index that will perform the singleLookup on the search words
     * @param multiWordWuery The query to match. each whitespace is treated as an AND condition and each " OR " is
     *                       treated as an OR condiiton
     * @return A list of websites matching at least one of the or conditions of the query.
     */
    public static List<Website> multiWordQuery2(Index index, String multiWordWuery) {
        // TODO: 31-Oct-17 This method can be improved by making a dictionary with all words in the query and
        // TODO: 31-Oct-17 corresponding search results so the same single query word is not looked up multiple times
        List<List<String>> splitQueries = modifyQuery(splitQuery(multiWordWuery));

        Set<Website> searchResults = new HashSet<>(); // This is the all the sites matching the full query
        // We loop over each OR separated list of query words
        for (int i = 0; i < splitQueries.size(); i++) {
            List<String> andSeperatedSearchWords = splitQueries.get(i);

            Set<Website> sites = new HashSet<>(); // This is the sites that matches all the and-separated-search-words

//            Get the query result for the first and-separated-search-words. This contains ALL sites matching the
//            and-separated-search-words (and probably also some additional ones).
            sites.addAll(index.lookup(andSeperatedSearchWords.get(0)));

            // Loop over the rest of the search words
            for (int j = 1; j < andSeperatedSearchWords.size(); j++) {
                List<Website> nextSites = index.lookup(andSeperatedSearchWords.get(j));
//                An intersection between sites and nextSites ensures to remove any pages not contained in all sites
//                matching at least one single word query.
                sites.retainAll(nextSites);
            }
            searchResults.addAll(sites);
        }
        return new ArrayList<>(searchResults);
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
        ArrayList<String> subList;
        //String line = query.trim();
        String[] array = new String[]{query};

        if (query.matches(".*?\\s+OR\\s+.*?")) {
            array = query.split("\\s+OR\\s+");
        }
        for (String sentence : array) {
            subList = new ArrayList<>();
            String[] temp = new String[]{sentence};
            if (sentence.contains(" ")) {
                temp = sentence.split(" ");
            }
            for (String words : temp) {
                if (!words.matches("") && words.length() > 0) {
                    String word = words.trim();
                    subList.add(word);
                }
            }
            if (!result.contains(subList) && subList.size() != 0) {
                result.add(subList);
            }
        }
        return result;
    }
}
