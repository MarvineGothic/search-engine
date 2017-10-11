import java.util.ArrayList;
import java.util.List;

public class IndexMethods {

    // testing method
    public static void main(String[] args) {
        String one = "the OR that nothing OR it OR bla bla bla OR bla bla bla";
        String two = "  two  ";
        String three = " one two  three    four";
        String four = "President USA OR Queen Danmark OR Chancellor Germany";
        String string = three;
        for (List<String> list : IndexMethods.splitQuery(string)) {
            for (String s : list) {
                System.out.println(s);
            }
            System.out.println();
        }
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
