package searchengine;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The FileHelper class loads and parses .txt files to extract data and create Website objects from that data. Before
 * creating a new object, site validity is checked.
 */
public class FileHelper {
    private static final String illegalCharacters = "̨̨̈̈.,:;-";
    private static final Set<String> usedUrls = new HashSet<>();
    private static String url;
    private static String title;
    private static List<String> listOfWords;

    /**
     * The method parses a .txt file line by line, checking which of three categories the line fits in: URL, Title or Word,
     * and reads it accordingly. The method calls the addSiteIfValid method to ensure website validity before adding to
     * the list of websites.
     *
     * @param fileDirectory a full directory + name of a given file containing data corresponding to website objects
     * @return a list of  website elements
     */
    public static List<Website> parseFile(String fileDirectory) {
        List<Website> sites = new ArrayList<>();
        resetWebsite();
        try (Scanner sc = new Scanner(new File(fileDirectory), "UTF-8")) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                if (line.startsWith("*PAGE:")) {
                    addSiteIfValid(url, title, listOfWords, sites);
                    resetWebsite();
                    url = line.substring(6);
                } else if (title.equals("")) {
                    title = line;
                } else {
                    String word = line.toLowerCase().trim();
                    listOfWords.add(word);
                }
            }
            addSiteIfValid(url, title, listOfWords, sites);
            usedUrls.clear();
            sc.close();
            return sites;
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't load the given file");
            e.printStackTrace();
            usedUrls.clear();
            return null;
        }
    }

    /**
     * This method takes four parameters - three of which are information about a given website, and the fourth is the
     * list of websites that have been parsed, to which we wish to add another website
     *
     * @param url         url of the site we wish to add to the list of sites
     * @param title       title of the site we wish to add to the list of sites
     * @param listOfWords keywords responding to the site we wish to add to the list of sites
     * @param sites       the list of sites
     */
    private static void addSiteIfValid(String url, String title, List<String> listOfWords, List<Website> sites) {
        if (checkForDuplicates(usedUrls, url)) {
            System.out.println("ERROR: Duplicate site when parsing file: " + url);
        }
        for (String word : listOfWords) {
            if (word.replaceAll("\\s", "").length() != word.length()) {
                System.out.println("ERROR: parseFile with multiple words on the same line: " + word);
            }
        }
        if (urlIsValid(url) && titleIsValid(title) && !listOfWords.isEmpty() && !checkForDuplicates(usedUrls, url)) {
            sites.add(new Website(url, title, listOfWords));
            usedUrls.add(url);
        }
    }

    /**
     * The method resets variables used in parsing and creating new website objects, to enable parsing of a new object
     * after the current one is done being created
     */
    private static void resetWebsite() {
        url = "";
        title = "";
        listOfWords = new ArrayList<>();
    }

    /**
     * Returns a HashSet with all the different words in a file.
     *
     * @param filename The filename to load
     * @return A set of all words on all the pages of that file.
     */
    public static HashSet<String> loadWordsInFile(String filename) {
        HashSet<String> words = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(getDataPath() + filename))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (wordIsValid(line)) words.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't load the given file");
            e.printStackTrace();
            return null;
        }
        return words;
    }

    /**
     * We check whether a given URL has already been added to a given database, by comparing it to a list of already
     * used URLS.
     *
     * @param usedUrls a list of URLs that have been added to the database already
     * @param url      an URL which is to be checked
     * @return returns true if the URL has been used before, and is in usedUrls, and false if the URL has not been
     * used before, and is not in usedUrls.
     */
    private static boolean checkForDuplicates(Set<String> usedUrls, String url) {
        return usedUrls.contains(url);
    }

    /**
     * Returns the full path to the data folder (in the root of your project), using the slashes native to your
     * operating system. For example: "C:\Users\Rasmus\Documents\Dropbox\Kurser\Programming Workshop\searchengine.SearchEngine\data\"
     *
     * @return Path to data folder
     */
    private static String getDataPath() {
        String dir = System.getProperty("user.dir");
        return dir + File.separator + "data" + File.separator;
    }

    /**
     * Loads a file similar to parse file, but only needs the file name instead of entire path
     * (assuming file is located in a folder called "data" in the root of the project).
     *
     * @param filename The file to load. For example "enwiki-small.txt".
     * @return A list of website objects
     */
    public static List<Website> loadFile(String filename) {
        return parseFile(getDataPath() + filename);
    }

    /**
     * Uses the imported UrlValidator method commons-validator-1.6.jar
     * Checks if URL contains illegal characters.
     *
     * @param URL The url in question.
     * @return Returns whether the url is valid or not.
     */
    private static boolean urlIsValid(String URL) {
        String[] schemes = {"http", "https", "ftp"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (URL.replaceAll("\\s", "").length() != URL.length()) {
            System.out.println("ERROR: parseFile with multiple words in the URL: *PAGE:" + URL);
        }
        return urlValidator.isValid(URL) && !URL.isEmpty();
    }

    /**
     * Checks if title contains illegal characters.
     *
     * @param title The title in question
     * @return Returns whether the title contains illegal characters or not.
     */
    private static boolean titleIsValid(String title) {
        return !(title.length() == 0) && !title.contains(illegalCharacters);
    }

    /**
     * Checks if word contains illegal characters.
     *
     * @param word The word in question.
     * @return Returns whether the word contains illegal characters or not.
     */
    private static boolean wordIsValid(String word) {
        return !word.contains(illegalCharacters);
    }

}

