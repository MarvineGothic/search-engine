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

        try {
            Scanner sc = new Scanner(new File(filename), "UTF-8");
            while (sc.hasNext()) {
                String line = sc.nextLine();
                if (line.startsWith("*PAGE:")) {
                    // create previous website from data gathered
                    if (!url.isEmpty() && !title.isEmpty() && title.trim().length() > 0 && !listOfWords.isEmpty()) {  // Sergiy
                        sites.add(new Website(url, title, listOfWords));
                    }
                    // new website starts
                    url = line.substring(6);
                    title = "";
                    listOfWords.clear();
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
                    } else listOfWords = null; //RasmusL: Remove statement?
                    // -----------------------------------------------------------------
                }
            }
            if (url != null && title != null && title.trim().length() > 0 && listOfWords != null) {    // Sergiy
                sites.add(new Website(url, title, listOfWords));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't load the given file");
            e.printStackTrace();
        }

        return sites;
    }
}
