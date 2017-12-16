package searchengine.Performance;

import searchengine.FileHelper;
import searchengine.IndexMethods;
import searchengine.Performance.BenchmarkingResources.IndexMethodsOld;
import searchengine.Ranking.Score;
import searchengine.Indexes.Index;
import searchengine.Ranking.BM25Score;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Website;

import java.util.List;


/**
 * This class measures the time needed to scan all the files chosen
 * for each of the 3 different indexes;
 * ReverseTreeMap, ReverseHashMap and Simple
 */

// TODO: 14-Dec-17 Clean this code, and consider using the BenchmarkTimer class
public class MultiWordQueryPerformance {
    public static void main(String[] args) {
        final int multiWordQuery = 1;
        final int multiWordQuery2 = 2;
        runTimeQuery(multiWordQuery2);
        runTimeQuery(multiWordQuery2);
        runTimeQuery(multiWordQuery);
        runTimeQuery(multiWordQuery2);
        runTimeQuery(multiWordQuery);
        runTimeQuery(multiWordQuery2);
        runTimeQuery(multiWordQuery);

    }

    /**
     * This method:
     * 1. loads a file.
     * 3. runs through the file for the word "and" to warm up.
     * 4. starts the timer.
     * 5. runs multiWordQuery
     * 6. and stops the time when finished.
     * 7. print out the result.
     *
     * @param method The index to check
     */

    public static void runTimeQuery(int method) {
        String query = "artist woman movie city OR danish home island snow tragedy OR theater people man crowd war OR civil war wanted never century OR fire OR swedish OR danish OR Australia";
        //Loading the file and the words
        List<Website> sites = FileHelper.loadFile("enwiki-medium.txt");
        Index index = new InvertedHashMapIndex();
        index.build(sites);
        Score ranker = new BM25Score(sites);
        long elapsedTime;

        //Adding a for-loop to warm up the test
        for (int i = 0; i < 1000; i++) {
            index.lookup("and");
        }
        //Running the actual test
        if (method == 1) {
            long startTime = System.nanoTime();
            IndexMethods.multiWordQuery(index, query, ranker);
            elapsedTime = System.nanoTime() - startTime;
        } else {
            long startTime = System.nanoTime();
            IndexMethodsOld.multiWordQuery(index, query, ranker);
            elapsedTime = System.nanoTime() - startTime;
        }
        //printing result
        System.out.printf("Method %s Took %d microseconds\n", method == 1 ? "multiWordQuery" : "multiWordQuery2", (elapsedTime / 1000));
    }
}