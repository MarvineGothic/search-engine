package Main;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */


public class IndexMethods {

    // testing method
    public static void main(String[] args) {
        List<List<String>> result;
        String one = "The OR That, nothing OR it OR bla bla. bla OR bla bla bla";
        String two = "  two  ";
        String three = " one two three four";
        String four = "President USA OR Queen Danmark OR Chancellor Germany";
        String string = one;

        IndexMethods im = new IndexMethods();

        for (List<String> list : im.splitQuery(string)) {
            for (String s : list) {
                System.out.println(s);
            }
            System.out.println();
        }
        result = splitQuery(string);
        System.out.println(result);
        result = im.modifyQuery(result);
        System.out.println(result);
    }


    /**
     * modifyQuery modifies the query, to ease use of regular expressions. This is done by the following:
     * 1. converting query words to lower-case
     * 2. converting any use of punctuation to single space
     * 3. converting any longer spaces in queries, e.g. uses of tab or double space to single space
     * 4. removing any space after a query word
     *
     * The modifyQuery method requires that queries have already been processed by the splitQuery method.
     *
     * @param query is a List of OR-statements, that are Lists of AND-statements.
     * @return returns a List of OR-statements, that are Lists of AND-statements, that have been converted to lower-case.
     *
     * When running this method we iterate through the Lists of strings in query, and within that iteration, we iterate through the strings
     * in those lists.
     */

    public static List<List<String>> modifyQuery(List<List<String>> query){
        List<List<String>> tempQuery = new ArrayList<>();
        for (List<String> subList: query) {
            String modifiedWord;
            List<String> tempSubList = new ArrayList<>();
            for (String word: subList) {
                modifiedWord = word.toLowerCase().replaceAll("\\p{Punct}"," ").replaceAll("//+s", " ")
                ;
                modifiedWord = modifiedWord.replaceAll("//s+$","");
                tempSubList.add((subList.indexOf(word)), modifiedWord);
            }
            tempQuery.add(query.indexOf(subList), tempSubList);
        }
        query = tempQuery;
        return query;
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
     *
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
