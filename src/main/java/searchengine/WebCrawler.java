package searchengine;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * <pre>
 * This class will start with website domain (fx http://gameofthrones.wikia.com/wiki/), and continue to search all
 * websites linked in that website as ling as the links are under the same domain.
 * For each website visited the website's url, title and html stripped text will be saved in format
 *      *PAGE:[url]
 *      [Title]
 *      Word1
 *      Word2
 *      ...
 *
 * Package dependencies:
 *      https://jsoup.org/download
 *      http://commons.apache.org/proper/commons-validator/download_validator.cgi
 *
 * Inspired by:
 *      https://www.mkyong.com/java/jsoup-basic-web-crawler-example/
 *      https://stackoverflow.com/questions/1600291/validating-url-in-java
 * </pre>
 */
public class WebCrawler {
    private final String illegalCharacters;
    private final UrlValidator urlValidator;
    private final Set<String> exploredLinks;
    private final Set<String> unexploredLinks;
    private FileWriter dataWriter;
    private String domain;
    private boolean continueCrawling = true;

    private WebCrawler(String startPage) throws Exception {
        illegalCharacters = "ÆÐƎƏƐƔĲŊŒẞÞǷȜæðǝəɛɣĳŋœĸſßþƿȝĄƁÇĐƊĘĦĮƘŁØƠŞȘŢȚŦŲƯY̨Ƴąɓçđɗęħįƙłøơşșţțŧųưy̨ƴÁÀÂÄǍĂĀÃÅǺĄÆǼǢƁĆĊĈČÇĎḌĐƊÐÉÈĖÊËĚĔĒĘẸƎƏƐĠĜǦĞĢƔáàâäǎăāãåǻąæǽǣɓćċĉčçďḍđɗðéèėêëěĕēęẹǝəɛġĝǧğģɣĤḤĦIÍÌİÎÏǏĬĪĨĮỊĲĴĶƘĹĻŁĽĿʼNŃN̈ŇÑŅŊÓÒÔÖǑŎŌÕŐỌØǾƠŒĥḥħıíìiîïǐĭīĩįịĳĵķƙĸĺļłľŀŉńn̈ňñņŋóòôöǒŏōõőọøǿơœŔŘŖŚŜŠŞȘṢẞŤŢṬŦÞÚÙÛÜǓŬŪŨŰŮŲỤƯẂẀŴẄǷÝỲŶŸȲỸƳŹŻŽẒŕřŗſśŝšşșṣßťţṭŧþúùûüǔŭūũűůųụưẃẁŵẅƿýỳŷÿȳỹƴźżžẓ";
        exploredLinks = new HashSet<>();
        unexploredLinks = new HashSet<>();
        urlValidator = new UrlValidator(new String[]{"http", "https", "ftp"});
        if (!urlValidator.isValid(startPage)) {
            System.out.println("Invalid starting page: " + startPage);
            return;
        }
        domain = startPage;


        // Make sure the data folder exists
        File file = new File(System.getProperty("user.dir") + File.separator + "data");
        String dir = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("new directory " + dir + " was created");
            } else {
                throw new FileNotFoundException("Could not find folder: " + dir + "\n and failed to create it.");
            }
        }

        String fileName = dir + domain.replaceAll("[/:]+", "-") + ".txt";
        String fileNameVisited = dir + domain.replaceAll("[/:]+", "-") + "unexplored.txt";

        // Check if file exists
        boolean fileExits1 = new File(fileName).isFile();
        boolean fileExits2 = new File(fileNameVisited).isFile();
        if (fileExits1 || fileExits2) {
            if (!(fileExits1 && fileExits2))
                throw new Exception("Both " + fileName + " and " + fileNameVisited + " must exist");
            // Load data
            boolean loaded = loadData();
            if (!loaded) {
                throw new FileNotFoundException("Failed to load data. Abort");
            }
        }

        try {
            dataWriter = new FileWriter(fileName, true);
        } catch (IOException e) {
            System.out.println("Failed to open the data file for writing");
            e.printStackTrace();
            return;
        }
        if (!exploredLinks.contains(startPage))
            unexploredLinks.add(startPage);
    }

    /**
     * <pre>
     * The main program will start to crawl the domain given until you ask it to quit or it cannot find any
     * urls matching the sub domain anymore.
     * @param args First element of args is the website domain (fx http://gameofthrones.wikia.com/wiki/)
     * </pre>
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Missing starting page argument");
            return;
        }
        WebCrawler webCrawler;
        try {
            webCrawler = new WebCrawler(args[0]);
            webCrawler.crawl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * This method loads the datafiles corresponding to the domain url, so you are ready to continue crawling
     * @return true if the files were successfully loaded. Otherwise false.
     * </pre>
     */
    private boolean loadData() {
        String dir = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        String fileName = dir + domain.replaceAll("[/:]+", "-") + ".txt";
        String fileNameVisited = dir + domain.replaceAll("[/:]+", "-") + "unexplored.txt";

        Scanner scanner;
        try {
            scanner = new Scanner(new File(fileNameVisited), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        while (scanner.hasNext()) {
            String url = scanner.nextLine();
            assert urlValidator.isValid(url);
            unexploredLinks.add(url);
        }

        try {
            scanner = new Scanner(new File(fileName), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.startsWith("*PAGE:")) {
                String url = line.replace("*PAGE:", "");
                assert urlValidator.isValid(url);
                exploredLinks.add(url);
            }
        }
        return true;
    }

    /**
     * <pre>
     * Finds the links of the given website and adds them to unexplored links if not yet visited. It also saves the
     * website's url, title and html-stripped text in the format
     *      *PAGE:[url]
     *      [Title]
     *      Word1
     *      Word2
     *      ...
     * @param url The website to be processed
     * </pre>
     */
    private void processLink(String url) {

        exploredLinks.add(url);

        // Fetch the HTML code
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.err.println("\nError: For '" + url + "': " + e.getMessage());
            return;
        }
        assert url.startsWith(domain);
        StringBuilder sb = new StringBuilder();
        sb.append("*PAGE:").append(url);
        String title = url.substring(domain.length(), url.length());
        if (title.equals(""))
            title = "Main-page";
        sb.append("\n").append(title);

        for (String word : document.text().split("\\s+")) {
            String strippedWord = wordStrip(word);
            if (!strippedWord.equals(""))
                sb.append("\n").append(strippedWord);
        }
        sb.append("\n");
        try {
            dataWriter.append(sb.toString());
        } catch (IOException e) {
            System.out.println("Failed to append to data file");
            e.printStackTrace();
        }

        // Parse the HTML to extract links to other URLs
        Elements linksOnPage = document.select("a[href]");

        // For each extracted URL add to visit list if unexplored
        for (Element page : linksOnPage) {
            String newUrl = page.attr("abs:href");
            if (exploredLinks.contains(newUrl) || !urlValidator.isValid(newUrl) || !newUrl.startsWith(domain))
                continue;
            unexploredLinks.add(newUrl);
        }
    }

    /**
     * <pre>
     * This methods does the following things:
     * - Replaces words containing illegal characters with an empty string.
     * - Replaces any valid url with an empty string
     * - Replaces words ending with .com with an empty string
     * - Replaces words longer than 25 characters with an empty string (this is normally script reference etc that was
     *   missed or some other kind of code that has nothing to do with the text of the website)
     * - Removes all characters not equal to a-z or A-Z.
     *
     * @param word:  Word to be stripped
     * @return stripped word
     * </pre>
     */
    private String wordStrip(String word) {
        if (word.contains(illegalCharacters))
            return "";
        if (urlValidator.isValid(word))
            return "";
        if (word.endsWith(".com"))
            return "";
        if (word.length() > 25)
            return "";
        return word.replaceAll("[^a-zA-Z]", "");
    }

    /**
     * <pre>
     * Saved a files with all unexplored links so if the program is terminated it can still load unexplored links
     * </pre>
     */
    private void saveUnexploredLinks() {

        String dir = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        String fileNameVisited = dir + domain.replaceAll("[/:]+", "-") + "unexplored.txt";

        // Tries 5 times if an error occurs.
        for (int j = 0; j < 5; j++) {
            try {
                FileWriter unexploredWriter = new FileWriter(fileNameVisited, false);
                for (String url : unexploredLinks) {
                    unexploredWriter.append(url).append("\n");
                }
                unexploredWriter.close();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new Error("Failed to save to unexplored file");
                }
            }
            throw new Error("Failed to save to unexplored file");
        }
    }

    /**
     * <pre>
     * This method handles the while loop that does the actual web-crawling
     * </pre>
     */
    private void crawl() {
        // Monitor for user termination
        Thread thread = new Thread(new AbortScanner(this));
        thread.start();

        System.out.println("Starting web crawling\n");
        int loopCount = 0;
        int interruptedSleepCount = -1;
        while (unexploredLinks.size() > 0) {
            loopCount += 1;

            // Sleep for 2 seconds to avoid overloading target server
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                interruptedSleepCount += 1;
            }
            if (!continueCrawling) {
                break;
            }

            String url = unexploredLinks.iterator().next();
            unexploredLinks.remove(url);
            System.out.println(url);
            processLink(url);
            // Continuously updates file with unexplored links in case the program is terminated
            saveUnexploredLinks();

            // Stop if to many interruptions errors
            if (interruptedSleepCount > loopCount / 5) {
                System.out.println("Error: Stopped crawling. To many interruptions");
                break;
            }
            /* TODO An additional improvement to this program would be to retry failed links once in a while out to
            TODO a maximum of 5 retries. This will ensure that errors because of temporary loss of connection etc
            TODO will not skip websites */
        }

        // Closes file
        try {
            dataWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to close to file");
            e.printStackTrace();
        }
        System.out.println("\n");
        if (continueCrawling)
            System.out.println("\nFinished crawling: " + domain);
        else
            System.out.println("\nCrawling of " + domain + " stopped\nProgress has been saved for next session");


    }

    /**
     * <pre>
     * This class is used in a thread that scans for the user to quit the process.
     * </pre>
     */
    private static class AbortScanner implements Runnable {
        private final WebCrawler crawler;

        AbortScanner(WebCrawler crawler) {
            this.crawler = crawler;
        }

        public void run() {
            Scanner sc = new Scanner(System.in);
            List<String> abortConditions = Collections.singletonList("q");

            System.out.println("type q (and enter) to quit");
            System.out.println("Your progress will be saved and you can continue later\n");
            while (sc.hasNext()) {
                String line = sc.nextLine();
                if (abortConditions.contains(line.toLowerCase().replaceAll("\n", ""))) {
                    crawler.continueCrawling = false;
                    System.out.println("Exiting web crawling...");
                    break;
                }
            }
        }
    }

}
