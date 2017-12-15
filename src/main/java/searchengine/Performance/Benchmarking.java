package searchengine.Performance;

import searchengine.FileHelper;
import searchengine.Indexes.Index;
import searchengine.Indexes.ReverseHashMapIndex;
import searchengine.Indexes.ReverseTreeMapIndex;
import searchengine.Indexes.SimpleIndex;
import searchengine.Website;

import java.util.List;
import java.util.Set;


/**
 * This class measures the time needed to scan all the files chosen
 * for each of the 3 different indexes;
 * ReverseTreeMap, ReverseHashMap and Simple
 */

// TODO: 14-Dec-17 Clean this code, and consider using the BenchmarkTimer class
public class Benchmarking {
    public static void main(String[] args) {
        runTimeIndex(new SimpleIndex());
        runTimeIndex(new ReverseTreeMapIndex());
        runTimeIndex(new ReverseHashMapIndex());
    }


    /**
     * This method:
     * 1. loads a file.
     * 2. scans it for words.
     * 3. runs through the file for the word "and" to warm up.
     * 4. starts the timer.
     * 5. looks up every word.
     * 6. and stops the time when finished.
     * 7. print out the result.
     *
     * @param index The index to check
     */

    public static void runTimeIndex(Index index) {
        //Loading the file and the words
        List<Website> sites = FileHelper.loadFile("enwiki-tiny.txt");
        Set<String> words = FileHelper.loadWordsInFile("enwiki-tiny.txt");
        index.build(sites);
        //Adding a for-loop to warm up the test
        for (int i = 0; i < 1000; i++) {
            for (String word : words) {
                index.lookup("and");
            }
        }
        //Running the actual test
        long startTime = System.nanoTime();
        for (String word : words) {
            index.lookup(word);
        }
        long elapsedTime = System.nanoTime() - startTime;
        //printing result
        System.out.println("Took "
                + (elapsedTime / 1000) + " microseconds for "
                + index.getClass().getSimpleName());
        System.out.println(" Words found : " + words.size());
    }
}