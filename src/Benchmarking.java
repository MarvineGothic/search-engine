import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Benchmarking {
    public static void main(String[] args) {
        runTimeIndex(new SimpleIndex());
        runTimeIndex(new ReverseTreeMapIndex());
        runTimeIndex(new ReverseHashMapIndex());
    }

    public static HashSet<String> loadFile(String filename) {
        HashSet<String> words = new HashSet<>();
        Scanner scanner;
        try {
            scanner = new Scanner(new File(FileHelper.getDataPath() + filename));

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't load the given file");
            e.printStackTrace();
            return null;
        }
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (!line.startsWith("*PAGE:")) {
                words.add(line);
            }
        }
        return words;
    }

    public static void runTimeIndex(Index index) {
        List<Website> sites = FileHelper.loadFile("enwiki-tiny.txt");
        Set<String> words = loadFile("enwiki-tiny.txt");
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