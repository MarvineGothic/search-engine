package Main.Performance;
import Main.Indexes.Index;
import Main.Indexes.ReverseHashMapIndex;
import Main.Indexes.ReverseTreeMapIndex;
import Main.Indexes.SimpleIndex;
import Main.FileHelper;
import Main.Website;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * This class measures the time needed for each of the 3 different indexes;
 *  - ReverseTreeMap, ReverseHashMap and Simple
 * to scan all the files chosen.
 */


public class Benchmarking {
    public static void main(String[] args) {
        runTimeIndex(new SimpleIndex());
        runTimeIndex(new ReverseTreeMapIndex());
        runTimeIndex(new ReverseHashMapIndex());
    }


    /**
     * This method:
     * 1. loads a file.
     * 2. scans it.
     * 3. starts the timer.
     * 4. looks up every word.
     * 5. and stops the time when finished.
     * @param index
     */

    public static void runTimeIndex(Index index) {
        List<Website> sites = FileHelper.loadFile("enwiki-tiny.txt");
        Set<String> words = FileHelper.loadWordsInFile("enwiki-tiny.txt");
        index.build(sites);
        long startTime = System.nanoTime();
        for (String word : words) {
            index.lookup(word);
        }
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Took "
                + (elapsedTime / 1000) + " microseconds for "
                + index.getClass().getSimpleName());
        System.out.println(" Words found : " + words.size());
    }
}