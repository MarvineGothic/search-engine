import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHelper {
    public static List<Website> parseFile(String filename) {
        List<Website> sites = new ArrayList<Website>();
        String url = "", title = "";
        List<String> listOfWords = new ArrayList<String>();

        //RL: Removed all null values from scanner to use empty instead
        try {
            Scanner sc = new Scanner(new File(filename), "UTF-8");
            while (sc.hasNext()) {
                String line = sc.nextLine();
                if (line.startsWith("*PAGE:")) {
                    // create previous website from data gathered
                    if (!url.isEmpty() && !title.isEmpty() && title.trim().length() > 0 && !listOfWords.isEmpty()) {  // Sergiy & RL
                        sites.add(new Website(url, title, listOfWords));
                    }
                    // new website starts
                    url = line.substring(6);
                    title = "";
                    listOfWords =  new ArrayList<String>();
                } else if (title.equals("")) {
                    title = line;
                } else {
                    // and that's a word!
                    if (listOfWords.isEmpty()) {
                        listOfWords = new ArrayList<String>();
                    }

                    /**
                     * @author Sergiy
                     */
                    String word = line.replaceAll(" ", "").toLowerCase().trim();
                    if (word.trim().length() != 0) {
                        listOfWords.add(word);
                    }
                }
            }
            if (!url.isEmpty() && !title.isEmpty() && title.trim().length() > 0 && !listOfWords.isEmpty()) {    // Sergiy & RL
                sites.add(new Website(url, title, listOfWords));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't load the given file");
            e.printStackTrace();
        }

        return sites;
    }

    /**
     * Author: Rasmus F
     * Returns the full path to the data folder (in the root of your project), using the slashes native to your
     * operating system. For example: "C:\Users\Rasmus\Documents\Dropbox\Kurser\Programming Workshop\SearchEngine\data\"
     * @return Path to data folder
     */
    public static String getDataPath(){
        String dir = System.getProperty("user.dir");
        return dir + File.separator + "data" + File.separator;
    }

    /**
     * Loads the a file similar to parse file, but only need the file name instead of entire path
     * (assuming file is located in a folder called "data" in the root of the project.
     * @param filename The file to load. For example "enwiki-small.txt".
     * @return A list of website objects
     */
    public static List<Website> loadFile(String filename){
        return parseFile(getDataPath() + filename);
    }
}
