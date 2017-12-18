package searchengine.CodeAnalysis;

import searchengine.CodeAnalysis.BenchmarkingResources.BM25ScoreNotIndexed;
import searchengine.FileHelper;
import searchengine.IndexMethods;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Ranking.*;
import searchengine.Website;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <pre>
 * This class will run do a perform a different multi word query whenever it is called. This is used to benchmark the
 * performance of calls using different implementations of the Score class.
 * </pre>
 */
public class RankerBenchmarking implements Callable<Integer> {
    private static List<String> queries;
    private static Index index;
    private Score ranker;
    private int currentQueryIndex = 0;

    /**
     * <pre>
     * @param ranker Generates a new instance with the specified Score.
     * </pre>
     */
    public RankerBenchmarking(Score ranker) {
        this.ranker = ranker;
    }

    /**
     * <pre>
     * This class simulates a number of multi word queries using different Score implementations and benchmarks the
     * performance.
     * @param args Is not used
     * </pre>
     */
    public static void main(String[] args) {
        String fileName = "enwiki-medium.txt";
        ArrayList<String> wordList = new ArrayList<>(FileHelper.loadWordsInFile(fileName));
        List<Website> sites = FileHelper.loadFile(fileName);
        index = new InvertedHashMapIndex();
        index.build(sites);

        int iterations = 10000;
        int warmUpIterations = Math.max(1, iterations / 100);

        queries = BenchmarkTimer.generateQueryList(wordList, 1, (iterations + warmUpIterations + 1));


        Score[] rankerList = new Score[]{
                new SimpleScore(),
                new BM25Score(sites),
                new BM25ScoreNotIndexed(sites),
                // Warm ups

                new SimpleScore(),
                new SimpleScore(),
                new BM25Score(sites),
                new BM25Score(sites),
                new BM25ScoreNotIndexed(sites),
                new BM25ScoreNotIndexed(sites),
        };

        for (int i = 0; i < rankerList.length; i++) {
            Score ranker = rankerList[i];
            Callable<Integer> callable = new RankerBenchmarking(ranker);
            String className = ranker.getClass().getSimpleName();
            try {
                BenchmarkTimer benchmark = new BenchmarkTimer(callable, iterations, warmUpIterations);
                System.out.println(className + ":");
                System.out.println(benchmark.toString());
            } catch (Exception e) {
                System.out.println("Benchmarking failed for " + className);
                e.printStackTrace();
            }
        }
    }

    @Override
    public Integer call() throws Exception {
        String query = queries.get(currentQueryIndex);
        currentQueryIndex++;
        IndexMethods.multiWordQuery(index, query, ranker);
        return 0;
    }
}
