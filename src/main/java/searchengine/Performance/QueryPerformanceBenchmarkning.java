package searchengine.Performance;

import bb.util.Benchmark;
import searchengine.FileHelper;
import searchengine.IndexMethods;
import searchengine.Indexes.IRanker;
import searchengine.Indexes.Index;
import searchengine.Indexes.RankerBM25;
import searchengine.Indexes.ReverseHashMapIndex;
import searchengine.Website;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * The purpose of this class is to benchmark the performance of multiWordQuery and multiWordQuery2 from IndexMethods
 * to see which method is more efficient.
 * This benchmarking class uses the package from this website: http://www.ellipticgroup.com/html/benchmarkingArticle.html
 */
public class QueryPerformanceBenchmarkning implements Callable<Integer> {
    private int iterations;
    private Index index;
    private List<String> queryList;
    private boolean methodType;
    private IRanker ranker;
    private int currentQueryIndex = 0;

    /**
     * Creates a new instance for performance testing.
     *
     * @param iterations How many lookups should be performed when (each  of) the performance call is executed
     * @param index      The index that performs the lookups
     * @param queryList  A list of queries that lookups should be done from (in order)
     *                   (which may contain more than one word and OR statements).
     * @param methodType if true, use method "multiWordQuery" otherwise "multiWordQuery2"
     * @param ranker
     */
    public QueryPerformanceBenchmarkning(int iterations, Index index, List<String> queryList, boolean methodType, IRanker ranker) {
        this.iterations = iterations;
        this.index = index;
        this.queryList = queryList;
        this.methodType = methodType;
        this.ranker = ranker;
    }

    /**
     * Run this method to benchmark multiWordQuery vs multiWordQuery2 method from IndexMethods.
     * It does so multiple times, once for different lengths of queries, and finally comparing the number of "wins" for
     * the two methods
     *
     * @param args These are not used
     */
    public static void main(String[] args) {
        int winsFor1 = 0;
        int winsFor2 = 0;
        for (int i = 2; i < 12; i++) {
            int winner = comparePerformance("enwiki-small.txt", i, 100000);
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
     *
     * @param filename      The filename loaded from data folder with websites to use for the index and to find lookup words
     * @param wordsPerQuery How many words should the queries have. (i.e. 3 means queries such as "cat dog mouse")
     * @param iterations    How many times should each of the to methods be tested
     * @return returns 1 if multiWordQuery is fastest, 2 if multiWordQuery2 is fastest
     */
    static private int comparePerformance(String filename, int wordsPerQuery, int iterations) {
        ArrayList<String> wordsInFile = new ArrayList<>(FileHelper.loadWordsInFile(filename));
        List<Website> sites = FileHelper.loadFile(filename);
        Index index = new ReverseHashMapIndex();
        index.build(sites);
        IRanker ranker = new RankerBM25(sites);
        System.out.println("Comparing multiWordQuery using wordsPerQuery: " + wordsPerQuery + ", iterations: " + iterations);

        int warmUpIterations = iterations / 100;
        List<String> queryList = generateQueryList(wordsInFile, wordsPerQuery, iterations + warmUpIterations);


//        Benchmarking multiWordQuery
//        --------------------------------------------------------------------------------------------------------------

        Callable callable1 = new QueryPerformanceBenchmarkning(iterations, index, queryList, true, ranker);
        Callable callable2 = new QueryPerformanceBenchmarkning(iterations, index, queryList, false, ranker);

        BenchmarkTimer benchmark1;
        BenchmarkTimer benchmark2;
        try {
            benchmark1 = new BenchmarkTimer(callable1, iterations, warmUpIterations);
            System.out.println("multiWordQuery");
            System.out.println(benchmark1.toString());
            benchmark2 = new BenchmarkTimer(callable2, iterations, warmUpIterations);
            System.out.println("multiWordQuery2");
            System.out.println(benchmark2.toString());
            if (benchmark1.getMeanRuntime() < benchmark2.getMeanRuntime())
                return 1;
            return 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Generates a list of queries, each containing a specified number of words. Words are selected using seed(0) so
     * this method will generate the same list each time as long as the input arguments are the same.
     *
     * @param wordList        The list of words to build the random queries from. (words are selected random)
     * @param wordsPerQuery   How many words should each query contain
     * @param numberOfQueries How many random queries do you want to generate
     * @return A list of string in the format af multi-word queries (without any OR statements)
     */
    private static List<String> generateQueryList(ArrayList<String> wordList, int wordsPerQuery, int numberOfQueries) {
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

    /**
     * This method performs the code to evaluate performance on. It uses the inputs provided in the constructor.
     *
     * @return Will always return 0 (should not be used for anything)
     * @throws Exception If something goes wrong it throws an exception.
     */
    public Integer call() throws Exception {
            if (methodType)
                IndexMethods.multiWordQuery(index, queryList.get(currentQueryIndex), ranker);
            else
                IndexMethods.multiWordQuery2(index, queryList.get(currentQueryIndex), ranker);
        currentQueryIndex++;
        return 0;
    }
}
