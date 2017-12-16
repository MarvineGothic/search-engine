package searchengine.Performance;

import searchengine.FileHelper;
import searchengine.IndexMethods;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Performance.BenchmarkingResources.IndexMethodsOld;
import searchengine.Ranking.BM25Score;
import searchengine.Ranking.Score;
import searchengine.Website;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <pre>
 * The purpose of this class is to benchmark the performance of multiWordQuery and multiWordQuery2 from IndexMethods
 * to see which method is more efficient.
 * This benchmarking class uses the package from this website: http://www.ellipticgroup.com/html/benchmarkingArticle.html
 * </pre>
 */
public class QueryPerformanceBenchmarkning implements Callable<Integer> {
    private Index index;
    private List<String> queryList;
    private boolean methodType;
    private Score ranker;
    private int currentQueryIndex = 0;

    /**
     * <pre>
     * Creates a new instance for performance testing.
     *
     * @param index      The index that performs the lookups
     * @param queryList  A list of queries that lookups should be done from (in order)
     *                   (which may contain more than one word and OR statements).
     * @param methodType if true, use method "multiWordQuery" otherwise "multiWordQuery2"
     * @param ranker
     * </pre>
     */
    public QueryPerformanceBenchmarkning(Index index, List<String> queryList, boolean methodType, Score ranker) {
        this.index = index;
        this.queryList = queryList;
        this.methodType = methodType;
        this.ranker = ranker;
    }

    /**
     * <pre>
     * Run this method to benchmark multiWordQuery vs multiWordQuery2 method from IndexMethods.
     * It does so multiple times, once for different lengths of queries, and finally comparing the number of "wins" for
     * the two methods
     *
     * @param args These are not used
     * </pre>
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
     * <pre>
     * This method runs both multiWordQuery vs multiWordQuery2 for the same queries a number of times and checks to
     * see which is the fastest
     *
     * @param filename      The filename loaded from data folder with websites to use for the index and to find lookup words
     * @param wordsPerQuery How many words should the queries have. (i.e. 3 means queries such as "cat dog mouse")
     * @param iterations    How many times should each of the to methods be tested
     * @return returns 1 if multiWordQuery is fastest, 2 if multiWordQuery2 is fastest
     * </pre>
     */
    static private int comparePerformance(String filename, int wordsPerQuery, int iterations) {
        ArrayList<String> wordsInFile = new ArrayList<>(FileHelper.loadWordsInFile(filename));
        List<Website> sites = FileHelper.loadFile(filename);
        Index index = new InvertedHashMapIndex();
        index.build(sites);
        Score ranker = new BM25Score(sites);
        System.out.println("Comparing multiWordQuery using wordsPerQuery: " + wordsPerQuery + ", iterations: " + iterations);

        int warmUpIterations = iterations / 100;
        List<String> queryList = BenchmarkTimer.generateQueryList(wordsInFile, wordsPerQuery,
                iterations + warmUpIterations);


        Callable<Integer> callable1 = new QueryPerformanceBenchmarkning(index, queryList, true, ranker);
        Callable<Integer> callable2 = new QueryPerformanceBenchmarkning(index, queryList, false, ranker);

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
     * <pre>
     * This method performs the code to evaluate performance on. It uses the inputs provided in the constructor.
     *
     * @return Will always return 0 (should not be used for anything)
     * @throws Exception If something goes wrong it throws an exception.
     * </pre>
     */
    public Integer call() throws Exception {
        if (methodType)
            IndexMethods.multiWordQuery(index, queryList.get(currentQueryIndex), ranker);
        else
            IndexMethodsOld.multiWordQuery(index, queryList.get(currentQueryIndex), ranker);
        currentQueryIndex++;
        return 0;
    }
}
