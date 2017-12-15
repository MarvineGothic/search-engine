package searchengine.Performance;

import searchengine.FileHelper;
import searchengine.IndexMethods;
import searchengine.Indexes.Index;
import searchengine.Indexes.ReverseHashMapIndex;
import searchengine.Ranking.*;
import searchengine.Website;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * <pre>
 * This class will run do a perform a different multi word query whenever it is called. This is used to benchmark the
 * performance of calls using different implementations of the IRanker class.
 * </pre> </pre>
 */
public class RankerBenchmarking implements Callable<Integer> {
    private static List<String> queries;
    private static Index index;
    private IRanker ranker;
    private int currentQueryIndex = 0;

    /**
     * <pre>
     * @param ranker Generates a new instance with the specified IRanker.
     * </pre>
     */
    public RankerBenchmarking(IRanker ranker) {
        this.ranker = ranker;
    }

    /**
     * <pre>
     * This class simulates a number of multi word queries using different IRanker implementations and benchmarks the
     * performance.
     * @param args Is not used
     * </pre>
     */
    public static void main(String[] args) {
        ArrayList<String> wordList = new ArrayList<String>(FileHelper.loadWordsInFile("enwiki-medium.txt"));
        List<Website> sites = FileHelper.loadFile("enwiki-medium.txt");
        index = new ReverseHashMapIndex();
        index.build(sites);

        int iterations = 10000;
        int warmUpIterations = Math.max(1, iterations / 100);

        queries = generateQueryList(wordList, (iterations + warmUpIterations));

        IRanker[] rankerList = new IRanker[]{
                new RankerBM25(sites),
                new RankerIDF(sites),
                new RankerNotIndexedIDF(sites),
                new RankerNotIndexedBM25(sites),
                new NoRanker(),
        };

        for (IRanker ranker : rankerList) {
            Callable<Integer> callable = new RankerBenchmarking(ranker);
            String className = ranker.getClass().getSimpleName();
            try {
                BenchmarkTimer benchmark = new BenchmarkTimer(callable, iterations, warmUpIterations);
                System.out.println(className + ":");
                System.out.println(benchmark.toString());

            } catch (Exception e) {
                System.out.println("Bencmarking failed for " + className);
                e.printStackTrace();
            }
        }
    }

    /**
     * <pre>
     * Generates a list of queries, each containing 1-5 random words from the wordList. Words are selected using seed(0) so
     * this method will generate the same list each time as long as the input arguments are the same.
     *
     * @param wordList        The list of words to build the random queries from. (words are selected random)
     * @param numberOfQueries How many random queries do you want to generate
     * @return A list of string in the format af multi-word queries (without any OR statements)
     * </pre>
     */
    private static List<String> generateQueryList(ArrayList<String> wordList, int numberOfQueries) {
        Random rnd = new Random();
        rnd.setSeed(0);
        List<String> queryList = new ArrayList<>();
        for (int i = 0; i < numberOfQueries; i++) {
            List<String> queryWords = new ArrayList<>();
            int wordsInQuery = rnd.nextInt(5) + 1;
            for (int j = 0; j < wordsInQuery; j++) {
                queryWords.add(wordList.get(rnd.nextInt(wordList.size())));
            }
            queryList.add(String.join(" ", queryWords));
        }
        return queryList;
    }

    @Override
    public Integer call() throws Exception {
        String query = queries.get(currentQueryIndex);
        currentQueryIndex++;
        IndexMethods.multiWordQuery(index, query, ranker);
        return 0;
    }
}
