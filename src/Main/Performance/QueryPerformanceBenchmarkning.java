package Main.Performance;

import Main.FileHelper;
import Main.IndexMethods;
import Main.Indexes.Index;
import Main.Indexes.ReverseHashMapIndex;
import Main.Website;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The purpose of this class is to benchmark the performance of multiWordQuery and multiWordQuery2 from IndexMethods
 * to see which method is more efficient
 */
public class QueryPerformanceBenchmarkning {

    /**
     * Run this method to benchmark multiWordQuery vs multiWordQuery2 method from IndexMethods.
     * It does so multiple times, once for different lengths of queries, and finally comparing the number of "wins" for
     * the two methods
     * @param args These are not used
     */
    public static void main(String[] args){
        // TODO: 01-Nov-17 The results varies every times the code runs. Would be nice to find a way to fix this 
        int winsFor1 = 0;
        int winsFor2 = 0;
        for (int i = 2; i < 12; i++) {
            int winner = comparePerformance("enwiki-small.txt", i, 10000);
            if (winner == 1)
                winsFor1++;
            else
                winsFor2++;
        }
        System.out.println(String.format("multiWordQuery had %s wins. multiWordQuery2 had %s wins", winsFor1, winsFor2));
        if (winsFor1 > winsFor2)
            System.out.println("multiWordQuery is the winner");
        else if (winsFor2 > winsFor1)
            System.out.println("multiWordQuery2 is the winner");
    }

    /**
     * This method runs both multiWordQuery vs multiWordQuery2 for the same queries a number of times and checks to
     * see which is the fastest
     * @param filename The filename loaded from data folder with websites to use for the index and to find lookup words
     * @param wordsPerQuery How many words should the queries have. (i.e. 3 means queries such as "cat dog mouse")
     * @param iterations How many times should each of the to methods be tested
     * @return returns 1 if multiWordQuery is fastest, 2 if multiWordQuery2 is fastest
     */
    static private int comparePerformance(String filename, int wordsPerQuery, int iterations){

        int warmupIterations = iterations / 10;
        ArrayList<String> wordsInFile = new ArrayList<>(FileHelper.loadWordsInFile(filename));
        List<Website> sites = FileHelper.loadFile(filename);
        Index index = new ReverseHashMapIndex();
        index.build(sites);
        System.out.println("Comparing multiWordQuery using wordsPerQuery: " + wordsPerQuery + ", iterations: " + iterations);

        List<String> queryList = generateQueryList(wordsInFile, wordsPerQuery, iterations + warmupIterations);

        int i;
        long startTime;
        long elapsedTime1;
        long elapsedTime2;

//        Benchmarking multiWordQuery1
//        --------------------------------------------------------------------------------------------------------------
//        Warm up
        for (i = 0; i < warmupIterations; i++){
            IndexMethods.multiWordQuery(index, queryList.get(i));
        }
        startTime = System.nanoTime();
        for (i = warmupIterations; i < iterations + warmupIterations; i++){
            IndexMethods.multiWordQuery(index, queryList.get(i));
        }
        elapsedTime1 = System.nanoTime() - startTime;

//        Benchmarking multiWordQuery2
//        --------------------------------------------------------------------------------------------------------------
        for (i = 0; i < warmupIterations; i++){
            IndexMethods.multiWordQuery2(index, queryList.get(i));
        }
        startTime = System.nanoTime();
        for (i = warmupIterations; i < iterations + warmupIterations; i++){
            IndexMethods.multiWordQuery2(index, queryList.get(i));
        }
        elapsedTime2 = System.nanoTime() - startTime;

//        Printing out results
//        --------------------------------------------------------------------------------------------------------------
        System.out.println("multiWordQuery: " + (elapsedTime1 / 1000) + " microseconds");
        System.out.println("multiWordQuery2: " + (elapsedTime2 / 1000) + " microseconds");
        System.out.println("--------------");

        if (elapsedTime1 < elapsedTime2)
            return 1;
        return 2;
    }

    /**
     * Generates a list of queries, each containing a specified number of words. Words are selected using seed(0) so
     * this method will generate the same list each time as long as the input arguments are the same.
     * @param wordList The list of words to build the random queries from. (words are selected random)
     * @param wordsPerQuery How many words should each query contain
     * @param numberOfQueries How many random queries do you want to generate
     * @return A list of string in the format af multi-word queries (without any OR statements)
     */
    private static List<String> generateQueryList(ArrayList<String> wordList, int wordsPerQuery, int numberOfQueries){
        Random rnd = new Random();
        rnd.setSeed(0);
        List<String> queryList = new ArrayList<>();
        for (int i = 0; i < numberOfQueries; i++) {
            List<String> queryWords = new ArrayList<>();

            for (int j = 0; j < wordsPerQuery; j++) {
                queryWords.add(wordList.get(rnd.nextInt(wordList.size())));
            }
            queryList.add(String.join(" ", queryWords));
        }
        return queryList;
    }
}
