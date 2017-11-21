package searchengine.Stemming;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Author: Rasmus F
 * This class can stem a text file encoded with utf-8 encoding
 */
public class Stemmer {

    /**
     * Author: Rasmus F
     * This method stems a single word. That is it reduces the word to the word to the base of the word
     * (fx "scattered" becomes "scatter").
     * Note: This method is far from perfect and sometimes reduces similar words with different meaning to the same base
     * For a better description read https://www.elastic.co/guide/en/elasticsearch/guide/current/stemming.html
     *
     */
    /**
     *
     * @param word The word to be stemmed
     * @return The stemmed word
     */
    public static String StemWord(String word){
        PortersStemmer stemmer = new PortersStemmer();
        stemmer.add(word.toCharArray(), word.length());
        stemmer.stem();
        return stemmer.toString();
    }

    /**
     * This method reads a number of files and applies stemming on each word. If the word has changed after the stemming
     * both the original and stemmed word is printed out.
     * Mainly used to test the StemWord method.
     * @param args A list of filenames. The filename paths are relative to the project directory
     */
    public static void main(String[] args) {
        final String dir = System.getProperty("user.dir");

        for (String filename : args) {
            filename = dir + File.separator + filename;
            Scanner scanner;
            try {
                scanner = new Scanner(new File(filename), "UTF-8");
            } catch (FileNotFoundException e) {
                System.out.println("Could not load file: " + filename);
                return;
            }
            while (scanner.hasNext()) {
                String line = scanner.nextLine().toLowerCase();
                String stemmedLine = Stemmer.StemWord(line);
                if (! line.equals(stemmedLine))
                    System.out.println(stemmedLine + " | " + line);
            }
        }
    }

}
