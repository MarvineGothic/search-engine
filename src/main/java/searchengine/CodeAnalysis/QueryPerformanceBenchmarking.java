package searchengine.CodeAnalysis;

import searchengine.FileHelper;
import searchengine.QueryHandler;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.CodeAnalysis.BenchmarkingResources.QueryHandlerOld;
import searchengine.Ranking.BM25Score;
import searchengine.Ranking.Score;
import searchengine.Website;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <pre>
 * The purpose of this class is to benchmark the performance of multiWordQuery from QueryHandler and QueryHandlerOld
 * to see which method is more efficient.
 * This benchmarking class uses the package from this website: http://www.ellipticgroup.com/html/benchmarkingArticle.html
 * </pre>
 */
public class QueryPerformanceBenchmarking implements Callable<Integer> {
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
     * @param methodType if true, use method "QueryHandler" otherwise "QueryHandlerOld"
     * @param ranker
     * </pre>
     */
    public QueryPerformanceBenchmarking(Index index, List<String> queryList, boolean methodType, Score ranker) {
        this.index = index;
        this.queryList = queryList;
        this.methodType = methodType;
        this.ranker = ranker;
    }

    /**
     * <pre>
     * Run this method to benchmark multiWordQuery method from QueryHandler vs QueryHandlerOld.
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
            int winner = comparePerformance("enwiki-small.txt", i, 10000);
            if (winner == 1)
                winsFor1++;
            else
                winsFor2++;
        }
        System.out.println(String.format("multiWordQuery had %s wins. multiWordQueryOld had %s wins", winsFor1, winsFor2));
        if (winsFor1 > winsFor2)
            System.out.println("multiWordQuery is the winner");
        else if (winsFor2 > winsFor1)
            System.out.println("multiWordQueryOld is the winner");
    }

    /**
     * <pre>
     * This method runs multiWordQuery from both QueryHandler and QueryHandlerOld for the same queries a number
     * of times and checks which is the fastest
     *
     * @param filename      The filename loaded from data folder with websites to use for the index and to find lookup words
     * @param wordsPerQuery How many words should the queries have. (i.e. 3 means queries such as "cat dog mouse")
     * @param iterations    How many times should each of the to methods be tested
     * @return returns 1 if QueryHandler is fastest, 2 if QueryHandlerOld is fastest
     * </pre>
     */
    static private int comparePerformance(String filename, int wordsPerQuery, int iterations) {
        ArrayList<String> wordsInFile = new ArrayList<>(FileHelper.loadWordsInFile(filename));
        List<Website> sites = FileHelper.loadFile(filename);
        Index index = new InvertedHashMapIndex();
        index.build(sites);
        Score ranker = new BM25Score(sites);
        System.out.println("Comparing multiWordQuery using wordsPerQuery: " + wordsPerQuery + ", iterations: " + iterations + "\n");

        int warmUpIterations = iterations / 100;
        List<String> queryList = Benchmark.generateQueryList(wordsInFile, wordsPerQuery,
                iterations + warmUpIterations);

        Callable<Integer> callable1 = new QueryPerformanceBenchmarking(index, queryList, true, ranker);
        Callable<Integer> callable2 = new QueryPerformanceBenchmarking(index, queryList, false, ranker);

        Benchmark benchmark1;
        Benchmark benchmark2;
        try {
            benchmark1 = new Benchmark(callable1, iterations, warmUpIterations);
            System.out.println(String.format("%-20s", "QueryHandler:") + benchmark1.toString());
            benchmark2 = new Benchmark(callable2, iterations, warmUpIterations);
            System.out.println(String.format("%-20s", "QueryHandlerOld:")+ benchmark2.toString());
            System.out.println("---------------------------------------------------");
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
    @Override
    public Integer call() throws Exception {
        if (methodType)
            QueryHandler.multiWordQuery(index, queryList.get(currentQueryIndex), ranker);
        else
            QueryHandlerOld.multiWordQuery(index, queryList.get(currentQueryIndex), ranker);
        currentQueryIndex++;
        return 0;
    }
}
