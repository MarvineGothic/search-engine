package Main;

import Main.Indexes.Index;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String string = five;

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
     * @param index The Index that will perform the singleLookup on the search words
     * @param multiWordWuery The query to match. each whitespace is treated as an AND condition and each " OR " is
     *                       treated as an OR condiiton
     * @return A list of websites matching at least one of the or conditions of the query.
     */
    public static List<Website> multiWordQuery(Index index, String multiWordWuery) {
        // TODO: 31-Oct-17 This method can be improved by making a dictionary with all words in the query and
        // TODO: 31-Oct-17 corresponding search results so the same single queary word is not looked up multiple times
        List<List<String>> splitQueries = modifyQuery(splitQuery(multiWordWuery));

        Set<Website> searchResults = new HashSet<>(); // This is the all the sites matching the full query
        // We loop over each OR separated list of query words
        for (int i = 0; i < splitQueries.size(); i++) {
            List<String> andSeperatedSearchWords = splitQueries.get(i);

            Set<Website> sites = new HashSet<>(); // This is the sites that matches all the and-separated-search-words

//            Get the query result for the first and-separated-search-words. This contains ALL sites matching the
//            and-separated-search-words (and probably also some additional ones).
            sites.addAll(index.singleLookup(andSeperatedSearchWords.get(0)));

            // Loop over the rest of the search words
            for (int j = 1; j < andSeperatedSearchWords.size(); j++) {
                List<Website> nextSites = index.singleLookup(andSeperatedSearchWords.get(j));
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
        String line = query.trim();
        String[] array = new String[]{line};

        if (line.contains(" OR ")) {
            array = line.split(" OR ");
        }
        for (String sentence : array) {
            subList = new ArrayList<>();
            String[] temp = new String[]{sentence};
            if (sentence.contains(" ")) {
                temp = sentence.split(" ");
            }
            for (String words : temp) {
                if (!words.matches("")) {
                    String word = words.trim();
                    subList.add(word);
                }
            }
            if (!result.contains(subList)) {
                result.add(subList);
            }
        }
        return result;
    }
}