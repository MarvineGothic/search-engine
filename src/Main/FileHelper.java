package Main;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {
    private static final Pattern CAPITALLETTERS = Pattern.compile("[A-Z0-9]");
    private static UrlValidator urlValidator;
    private static String illegalCharacters = "ÆÐƎƏƐƔĲŊŒẞÞǷȜæðǝəɛɣĳŋœĸſßþƿȝĄƁÇĐƊĘĦĮƘŁØƠŞȘŢȚŦŲƯY̨Ƴąɓçđɗęħįƙłøơşșţțŧųưy̨ƴÁÀÂÄǍĂĀÃÅǺĄÆǼǢƁĆĊĈČÇĎḌĐƊÐÉÈĖÊËĚĔĒĘẸƎƏƐĠĜǦĞĢƔáàâäǎăāãåǻąæǽǣɓćċĉčçďḍđɗðéèėêëěĕēęẹǝəɛġĝǧğģɣĤḤĦIÍÌİÎÏǏĬĪĨĮỊĲĴĶƘĹĻŁĽĿʼNŃN̈ŇÑŅŊÓÒÔÖǑŎŌÕŐỌØǾƠŒĥḥħıíìiîïǐĭīĩįịĳĵķƙĸĺļłľŀŉńn̈ňñņŋóòôöǒŏōõőọøǿơœŔŘŖŚŜŠŞȘṢẞŤŢṬŦÞÚÙÛÜǓŬŪŨŰŮŲỤƯẂẀŴẄǷÝỲŶŸȲỸƳŹŻŽẒŕřŗſśŝšşșṣßťţṭŧþúùûüǔŭūũűůųụưẃẁŵẅƿýỳŷÿȳỹƴźżžẓ";

    public static List<Website> parseFile(String filename) {
        List<Website> sites = new ArrayList<Website>();
        String url = "", title = "";
        List<String> listOfWords = new ArrayList<String>();


        //RL: Removed all null values from scanner to use empty instead
        Scanner sc = null;
        try {
            sc = new Scanner(new File(filename), "UTF-8");
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't load the given file");
            e.printStackTrace();
            return null;
        }

        Set<String> usedUrls = new HashSet<>(); // Keeps track of url so we don't get duplicate sites
        while (sc.hasNext()) {
            String line = sc.nextLine();

            if (line.startsWith("*PAGE:")) {
                // create previous website from data gathered
                if (!url.isEmpty() && !title.isEmpty() && title.trim().length() > 0 && !listOfWords.isEmpty()) {  // Sergiy & RL
                    if (checkForDublicates(usedUrls, url))
                        return null;
                    sites.add(new Website(url, title, listOfWords));
                }

                // new website starts
                // Check for multiple words
                url = line.substring(6);
                if (line.replaceAll("\\s", "").length() != line.length() || !urlIsValid(url)) { // added !urlIsValid here for now -Atoe-
                    System.out.println("ERROR: parseFile with multiple words int the URL: " + line);
                    return null;
                }

                title = "";
                listOfWords = new ArrayList<String>();
            } else if (title.equals("")) { //added titleIsValid here for now -atoe-
                if (titleIsValid(line)) {
                    title = line;
                } else {
                    System.out.println("ERROR: Invalid Title: " + line);
                    return null;
                }
            } else {
                // Check for multiple words
                if (!line.startsWith("*PAGE:") && line.replaceAll("\\s", "").length() != line.length()) {
                    System.out.println("ERROR: parseFile with multiple words on the same line: " + line);
                    return null;
                }
                // and that's a word!
                if (listOfWords.isEmpty()) {
                    listOfWords = new ArrayList<String>();
                }
                String word = line.replaceAll(" ", "").toLowerCase().trim();
                if (word.trim().length() != 0 && wordIsValid(word)) { // Added wordIsValid here for now -atoe-
                    listOfWords.add(word);
                }
            }
        }
        if (!url.isEmpty() && !title.isEmpty() && title.trim().length() > 0 && !listOfWords.isEmpty()) {
            if (checkForDublicates(usedUrls, url))
                return null;
            sites.add(new Website(url, title, listOfWords));
        }

        return sites;
    }

    /**
     * Returns a HashSet with all the different words in a file.
     *
     * @param filename The filename to load
     * @return A set of all words on all the pages of that file.
     */
    public static HashSet<String> loadWordsInFile(String filename) {
        HashSet<String> words = new HashSet<>();
        Scanner scanner;
        try {
            scanner = new Scanner(new File(getDataPath() + filename));

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't load the given file");
            e.printStackTrace();
            return null;
        }
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.startsWith("*PAGE:")) { //should wordIsValid be added here? -atoe-
                scanner.nextLine(); // Skips the title of the page as well
            } else {
                words.add(line);

            }
        }
        return words;
    }

    private static boolean checkForDublicates(Set<String> usedUrls, String url) {
        // Check if site is a duplicate;
        if (usedUrls.contains(url)) {
            System.out.println("ERROR: Duplicate site when parsing file: " + url);
            return true;
        }
        usedUrls.add(url);
        return false;
    }


    /**
     * Author: Rasmus F
     * Returns the full path to the data folder (in the root of your project), using the slashes native to your
     * operating system. For example: "C:\Users\Rasmus\Documents\Dropbox\Kurser\Programming Workshop\Main.SearchEngine\data\"
     *
     * @return Path to data folder
     */
    public static String getDataPath() {
        String dir = System.getProperty("user.dir");
        return dir + File.separator + "data" + File.separator;
    }

    /**
     * Loads the a file similar to parse file, but only need the file name instead of entire path
     * (assuming file is located in a folder called "data" in the root of the project.
     *
     * @param filename The file to load. For example "enwiki-small.txt".
     * @return A list of website objects
     */
    public static List<Website> loadFile(String filename) {
        return parseFile(getDataPath() + filename);
    }


    /**
     * atoe
     * Uses the imported UrlValidator method commons-validator-1.6.jar
     * Checks if URL contains illegal characters.
     *
     * @param URL The url in question.
     * @return Returns whether the url is valid or not.
     */
    public static boolean urlIsValid(String URL) {
        String[] schemes = {"http", "https", "ftp"};
        urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(URL) && !URL.contains(illegalCharacters)) {
            return true;
        } else
            System.out.println("Invalid URL: " + URL);
            return false;
    }

    /**
     * atoe
     * Checks if first letter is uppercase by Regex.
     * Checks if title contains illegal characters.
     *
     * @param title The title in question
     * @return Returns whether the title is a title with uppercase or not.
     */
    public static boolean titleIsValid(String title) {
        if (title.length() == 0)
            return false;
        String firstLetter = String.valueOf(title.charAt(0));
        Matcher ttl = CAPITALLETTERS.matcher(firstLetter);
        if(!title.contains(illegalCharacters) && ttl.matches()){
//            System.out.println("titleIsValid passed the title");
            return true;
        } else {
//            System.out.println("titleIsValid didn't pass the title: " + title);
            return false;
        }
    }

    /**
     * atoe
     * Checks if word contains illegal characters.
     *
     * @param word The word in question.
     * @return Returns whether the word is a word or not.
     */
    public static boolean wordIsValid(String word) {
        return !word.contains(illegalCharacters);
    }

}

