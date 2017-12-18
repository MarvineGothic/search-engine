package searchengine.CodeAnalysis;

import searchengine.FileHelper;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Indexes.InvertedTreeMapIndex;
import searchengine.Indexes.SimpleIndex;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * <pre>
 * This class test the performance of the lookup method for different implementations Index.
 * The indexes tested are;
 * ReverseTreeMap, ReverseHashMap and Simple
 * </pre>
 */
public class IndexBenchmarking implements Callable<Integer> {
    private static List<String> listOfQueries;
    private Index index;
    private int currentIndex = 0;

    /**
     * <pre>
     * Creates a new instance and assigns the specified Index used when doing the benchmark.
     * @param index The associated Index
     * </pre>
     */
    private IndexBenchmarking(Index index) {
        this.index = index;
    }

    /**
     * <pre>
     * This class test the performance of the lookup method for different implementations Index.
     * The indexes tested are;
     * ReverseTreeMap, ReverseHashMap and Simple
     * @param args The first element in args specifies the filename. The rest of the args are ignored. If args is empty
     *             the filename defaults to "enwiki-medium.txt".
     * </pre>
     */
    public static void main(String[] args) {
        int iterations = 5000;
        int warmUpIterations = Math.max(1, iterations/100);
        String filename = "enwiki-medium.txt";
        if (args.length > 0) {
            filename = args[0];
        }
        ArrayList<String> wordList = new ArrayList<>(FileHelper.loadWordsInFile(filename));
        List<Website> websiteList = FileHelper.loadFile(filename);
        listOfQueries = Benchmark.generateQueryList(wordList, 1, iterations + warmUpIterations);

        List<Index> indexList = Arrays.asList(
                new SimpleIndex(),
                new InvertedTreeMapIndex(),
                new InvertedHashMapIndex()
        );

        for (Index index : indexList) {
            index.build(websiteList);
            try {
                Benchmark benchmark = new Benchmark(new IndexBenchmarking(index), iterations, warmUpIterations);
                String name = index.getClass().getSimpleName();
                System.out.println(String.format("%-25s", name + ":") + benchmark.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Integer call() throws Exception {
        String query = listOfQueries.get(currentIndex);
        index.lookup(query);
        currentIndex++;
        return 0;
    }
}