package searchengine.CodeAnalysis;

import searchengine.FileHelper;
import searchengine.IndexMethods;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Ranking.*;
import searchengine.Website;

import java.util.*;

/** <pre>
 * This class provides functionality to print a matrix (List of lists of strings) and to get the top 10 search results
 * for different implementations of the Score interface to see how the ranking compares to each other.
 */
public class RankingResultComparison {
    private List<Score> scoreList;
    private Index index;

    /** <pre>
     * Prints out the top 10 search results for different implementation of the Score interface for a list of quries
     * to easily compare results
     * @param args A list of queries. Each query can contain more than one word and you can use OR the create
     *             or-conditions
     */
    public static void main(String[] args){
        if (args.length == 0){
            args = new String[]{
                    "america",
                    "queen",
                    "animal",
                    "computer OR PC OR laptop",
                    "clothes",
                    "president america OR united states OR queen england",
            };
        }
        new RankingResultComparison("enwiki-medium.txt", args);
    }

    /** <pre>
     * Prints out the top 10 search results for different implementation of the Score interface for a list of quries
     * to easily compare results
     * @param queries A list of queries. Each query can contain more than one word and you can use OR the create
     *             or-conditions
     */
    public RankingResultComparison(String filename, String[] queries){
        List<Website> listOfWebsites = FileHelper.loadFile(filename);
        scoreList = new ArrayList<>();
        scoreList.add(new BM25Score(listOfWebsites));
        scoreList.add(new TFIDFScore(listOfWebsites));
        scoreList.add(new TFScore());
        scoreList.add(new IDFScore(listOfWebsites));
        scoreList.add(new SimpleScore());
        index = new InvertedHashMapIndex();
        index.build(listOfWebsites);

        for (String query : queries) {
            compareSearchResults(query);
        }
    }

    /** <pre>
     * This method shows the top 10 search results for different Score implemetation to easily compare the outputs.
     * @param query The given query. Can contain more than one word and you can use OR the create or-conditions
     */
    private void compareSearchResults(String query) {
        int shownSearchResults = 10;
        int maxStringLength = 0;

        List<List<String>> resultMatrix = new ArrayList<>();

        Map<Score, List<Website>> resultDict = new HashMap<>();
        List<String> headers = new ArrayList<>();
        headers.add("Rank/Scorer");

        for (Score score : scoreList) {
            headers.add(score.getClass().getSimpleName());
            List<Website> lookupResult = IndexMethods.multiWordQuery(index, query, score);
            for (int i = 0; i < Math.min(shownSearchResults, lookupResult.size()); i++) {
                maxStringLength = Math.max(lookupResult.get(i).getTitle().length(), maxStringLength);
            }
            resultDict.put(score, lookupResult);
        }
        resultMatrix.add(headers);

        for (Integer i = 0; i < shownSearchResults; i++) {
            List<String> row = new ArrayList<>();
            row.add(i.toString());
            for (Score score : scoreList) {
                List<Website> resultList = resultDict.get(score);
                try {
//                    row.add(resultList.get(i).getUrl().replace("https://en.wikipedia.org/", ""));
                    row.add(resultList.get(i).getTitle());
                } catch (IndexOutOfBoundsException e){
                    break;
                }
            }
            if (row.size() > 1)
                resultMatrix.add(row);
        }
        System.out.println("Results for query: " + query);
        printMatrix(resultMatrix);
    }

    /** <pre>
     * This method prints a matrix (list of list of strings) in a nice formatted way.
     * @param matrix A list of lists of strings, representing a "matrix".
     */
    public void printMatrix(List<List<String>> matrix){
        String separator = ";";

        Map<Integer, Integer> rowWidthMap = new HashMap<>();
        for (Integer i = 0; i < matrix.size(); i++) {
            List<String> row = matrix.get(i);
            for (int j = 0; j < row.size(); j++) {
                String element = row.get(j);
                int maxStringLength = rowWidthMap.getOrDefault(j, 1);
                maxStringLength = Math.max(maxStringLength, element.length());
                rowWidthMap.put(j, maxStringLength);
            }
        }

        StringBuilder sb = new StringBuilder();

        for (List<String> row : matrix) {
            for (int i = 0; i < row.size(); i++) {
                String element = row.get(i);
                int maxStringLength = rowWidthMap.getOrDefault(i, 1);
                String format = "%-" + (maxStringLength) + "s " + separator;
                sb.append(String.format(format, element));
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
