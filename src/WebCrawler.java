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
 * Author: Rasmus F
 * This class will start with webpage domain (fx http://gameofthrones.wikia.com/wiki/), and continue to search all
 * webpages linked in that webpage as ling as the links are under the same domain.
 * For each webpage visited the webpage's url, title and html stripped text will be saved in format
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
 */
public class WebCrawler {
    private String illgalCharacters;
    private FileWriter dataWriter;
    private String domain;
    private UrlValidator urlValidator;
    private HashSet<String> exploredLinks;
    private HashSet<String> unexploredLinks;
    private HashMap<String, Integer> retryLinks; // Hashmap of failed links: Key: urls, Value: Number of failed attempts
    private boolean continueCrawling = true;

    /**
     * The main program will start to crawl the domain given until you ask it to quit or it cannot find any
     * urls mathing the sub domain anymore.
     * @param args First element of args is the webpage domain (fx http://gameofthrones.wikia.com/wiki/)
     */
    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("Missing starting page argument");
            return;
        }
        WebCrawler webCrawler = new WebCrawler(args[0]);
        webCrawler.crawl();
    }


    private WebCrawler(String startPage){
        illgalCharacters = "ÆÐƎƏƐƔĲŊŒẞÞǷȜæðǝəɛɣĳŋœĸſßþƿȝĄƁÇĐƊĘĦĮƘŁØƠŞȘŢȚŦŲƯY̨Ƴąɓçđɗęħįƙłøơşșţțŧųưy̨ƴÁÀÂÄǍĂĀÃÅǺĄÆǼǢƁĆĊĈČÇĎḌĐƊÐÉÈĖÊËĚĔĒĘẸƎƏƐĠĜǦĞĢƔáàâäǎăāãåǻąæǽǣɓćċĉčçďḍđɗðéèėêëěĕēęẹǝəɛġĝǧğģɣĤḤĦIÍÌİÎÏǏĬĪĨĮỊĲĴĶƘĹĻŁĽĿʼNŃN̈ŇÑŅŊÓÒÔÖǑŎŌÕŐỌØǾƠŒĥḥħıíìiîïǐĭīĩįịĳĵķƙĸĺļłľŀŉńn̈ňñņŋóòôöǒŏōõőọøǿơœŔŘŖŚŜŠŞȘṢẞŤŢṬŦÞÚÙÛÜǓŬŪŨŰŮŲỤƯẂẀŴẄǷÝỲŶŸȲỸƳŹŻŽẒŕřŗſśŝšşșṣßťţṭŧþúùûüǔŭūũűůųụưẃẁŵẅƿýỳŷÿȳỹƴźżžẓ";
        exploredLinks = new HashSet<>();
        unexploredLinks = new HashSet<>();
        retryLinks = new HashMap<>();
        urlValidator = new UrlValidator(new String[]{"http", "https", "ftp"});
        if (!urlValidator.isValid(startPage)) {
            System.out.println("Invalid starting page: " + startPage);
            return;
        }
        domain = startPage;

        String dir = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        String fileName = dir + domain.replaceAll("[/:]", "_") + ".txt";
        String fileNameVisited = dir + domain.replaceAll("[/:]", "_") + "_Unexplored.txt";

        // Check if file exists
        boolean fileExits1 = new File(fileName).isFile();
        boolean fileExits2 = new File(fileNameVisited).isFile();
        if (fileExits1 || fileExits2){
            if (!(fileExits1 && fileExits2))
                throw new Error("Both " + fileName + " and " + " must exist");
            // Load data
            boolean loaded = loadData();
            if (!loaded) {
                throw new Error("Failed to load data. Abort");
            }
        }

        try {
            dataWriter = new FileWriter(fileName,true);
        } catch (IOException e) {
            System.out.println("Failed to open the data file for writing");
            e.printStackTrace();
            return;
        }

        unexploredLinks.add(startPage);
    }

    /**
     * This method loads the datafiles corresponding to the domain url, so you are ready to continue crawling
     * @return true if the files were successfully loaded. Otherwise false.
     */
    private boolean loadData(){
        String dir = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        String fileName = dir + domain.replaceAll("[/:]", "_") + ".txt";
        String fileNameVisited = dir + domain.replaceAll("[/:]", "_") + "_Unexplored.txt";

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
            if (line.startsWith("*PAGE:")){
                String url = line.replace("*PAGE:", "");
                assert urlValidator.isValid(url);
                exploredLinks.add(url);
            }
        }
        return true;
    }

    /**
     * Finds the links of the given webpage and adds them to unexplored links if not yet visited. It also saves the
     * webpage's url, title and html-stripped text in the format
     *      *PAGE:[url]
     *      [Title]
     *      Word1
     *      Word2
     *      ...
     * @param url The webpage to be processed
     */
    private void processLink(String url){

        exploredLinks.add(url);

        // Fetch the HTML code
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            retryLinks.merge(url, 1, (a, b) -> a + b); // increment failed attempt with 1.
            System.err.println("\nError: For '" + url + "': " + e.getMessage());
            return;
        }
        assert url.startsWith(domain);
        StringBuilder sb = new StringBuilder();
        sb.append("*PAGE:").append(url);
        String title = url.substring(domain.length(), url.length());
        if (title.equals(""))
            title = "Mainpage";
        sb.append("\n").append(title);

        // TODO Maybe add features to only parse selected html elements (custom for different domains)
        for (String word : document.text().split(" ")){
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
     * Author: Rasmus F
     * strips a word to remove illigal characters (fx ,.'?!)
     * It also removes foreign words such as é, à or á.
     * Finaly it removes some webpages
     * @param word:  Word to be stripped
     * @return stripped word
     */
    private String wordStrip(String word){
        if (word.contains(illgalCharacters))
            return "";
        if (word.contains(domain))
            return "";
        if (word.endsWith(".com"))
            return "";
        return word.replaceAll("[^a-zA-Z]", "");
    }

    /**
     * Saved a files with all unexplored links so if the program is terminated it can still load unexplored links
     */
    private void saveUnexploredLinks(){

        String dir = System.getProperty("user.dir") + File.separator + "data" + File.separator;
        String fileNameVisited = dir + domain.replaceAll("[/:]", "_") + "_Unexplored.txt";

        // Tries 5 times if an error occurs.
        for (int j = 0; j < 5; j++){
            try {
                FileWriter unexploredWriter = new FileWriter(fileNameVisited, false);
                for (String url : unexploredLinks){
                    unexploredWriter.append(url + "\n");
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
     * This class is used in a thread that scans for the user to quit the process.
     */
    private static class AbortScanner implements Runnable {
        private WebCrawler crawler;

        AbortScanner(WebCrawler crawler){
            this.crawler = crawler;
        }

        public void run() {
            Scanner sc = new Scanner(System.in);
            List<String> abortConditions = new ArrayList<>();
            abortConditions.add("q");

            System.out.println("type q (and enter) to quit");
            System.out.println("Your progress will be saved and you can continue later\n");
            while (sc.hasNext()) {
                String line = sc.nextLine();
                if (abortConditions.contains(line.toLowerCase())) {
                    crawler.continueCrawling = false;
                    break;
                }
            }
        }
    }


    /**
     * This method handles the while loop that does the actual web-crawling
     */
    private void crawl(){
        // Monitor for user termination
        Thread thread = new Thread(new AbortScanner(this));
        thread.start();

        System.out.println("Starting web crawling\n");
        int loopcount = 0;
        int interruptetSleepCount = -1;
        while (unexploredLinks.size() > 0 && continueCrawling){
            loopcount += 1;

            // Sleep for 2 seconds to avoid overloading target server
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException ex)
            {
                interruptetSleepCount += 1;
            }

            String url = unexploredLinks.iterator().next();
            unexploredLinks.remove(url);
            System.out.println(url);
            processLink(url);
            // Continuously updates file with unexplored links in case the program is terminated
            saveUnexploredLinks();


            // Stop if to many interruptions errors
            if (interruptetSleepCount > loopcount / 5){
                System.out.println("Eroor: Stopped crawling. To many interruptions");
                break;
            }
            // TODO Retry some of the failed links again until they have been tried lets say 5 times.
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
            System.out.println("\nCrawling of " + domain + "stopped\nProgress has been saved for next session");


    }


}
