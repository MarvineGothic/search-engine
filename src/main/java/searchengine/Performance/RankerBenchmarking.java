package searchengine.Performance;

import bb.util.Benchmark;
import searchengine.FileHelper;
import searchengine.IndexMethods;
import searchengine.Indexes.*;
import searchengine.Website;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class RankerBenchmarking implements Callable<Integer> {
    private static List<String> queries;
    private static Index index;
    private IRanker ranker;


    public RankerBenchmarking(IRanker ranker) {
        this.ranker = ranker;
    }

    public static void main(String[] args) {
        ArrayList<String> wordList = new ArrayList<String>(FileHelper.loadWordsInFile("enwiki-small.txt"));
        List<Website> sites = FileHelper.loadFile("enwiki-small.txt");
        index = new ReverseHashMapIndex();
        index.build(sites);
        queries = generateQueryList(wordList, 100);

        IRanker[] rankerList = new IRanker[]{
                new RankerBM25(sites),
                new RankerIDF(sites),
                new NoRanker(),
        };

        for (IRanker ranker : rankerList) {
            Callable<Integer> callable = new RankerBenchmarking(ranker);
            String className = ranker.getClass().getSimpleName();
            try {
                Benchmark benchmark1 = new Benchmark(callable);
//                System.out.println("Average time for " + className + ": " + benchmark1.getMean());
                System.out.println(className + ":");
                System.out.println(benchmark1.toString());

            } catch (Exception e) {
                System.out.println("Bencmarking failed for " + className);
                e.printStackTrace();
            }

        }

    }

    /**
     * Generates a list of queries, each containing 1-5 random words from the wordList. Words are selected using seed(0) so
     * this method will generate the same list each time as long as the input arguments are the same.
     *
     * @param wordList        The list of words to build the random queries from. (words are selected random)
     * @param numberOfQueries How many random queries do you want to generate
     * @return A list of string in the format af multi-word queries (without any OR statements)
     */
    private static List<String> generateQueryList(ArrayList<String> wordList, int numberOfQueries) {
        Random rnd = new Random();
        rnd.setSeed(0);
        List<String> queryList = new ArrayList<>();
        for (int i = 0; i < numberOfQueries; i++) {
            List<String> queryWords = new ArrayList<>();
            int wordInQuery = rnd.nextInt(5) + 1;
            for (int j = 0; j < wordInQuery; j++) {
                queryWords.add(wordList.get(rnd.nextInt(wordList.size())));
            }
            queryList.add(String.join(" ", queryWords));
        }
        return queryList;
    }

    @Override
    public Integer call() throws Exception {
        for (String query : queries) {
            IndexMethods.multiWordQuery(index, query, ranker);
        }
        return 0;
    }


}
