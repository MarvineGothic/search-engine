import java.io.File;
import java.util.*;

public class SimpleIndex implements Index {


    public static void main(String[] args) {
        SimpleIndex simpleIndex = new SimpleIndex();
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the SearchEngine!");
        if (args.length <= 0) {
            System.out.println("Error: Filename is missing");
            return;
        }
        // Now you only need to specify the relative path from the project folder to the datafile
        String dir = System.getProperty("user.dir");
        List<Website> sites = FileHelper.parseFile(dir + File.separator + "data" + File.separator + args[0]);
        // ----------------------------------------------------------------


        simpleIndex.build(sites);

        System.out.println("These are some of the available sites");
        for (int i = 0; i < Math.min(10, sites.size()); i++) {
            System.out.println(sites.get(i));
        }
        System.out.println("Please provide a query word");

        while (sc.hasNext()) {
            String line = sc.nextLine();
            simpleIndex.lookup(line);
        }
    }

    private List<Website> sites;

    SimpleIndex() {
        this.sites = new ArrayList<>();
    }

    /**
     *
     * @param websiteList The full list of websites that should be processed
     *                    it "builds" a List of websites from given parameter
     *                    then removes repeated words and sort in alphabetic order
     */
    @Override
    public void build(List<Website> websiteList) {
        // sites = websiteList;
        // uncomment previous line and comment the for loop if we will not use it ))
        for (Website website : websiteList) {
            List<String> first = website.getWords();
            ArrayList<String> result = new ArrayList<String>(new HashSet<String>(first));
            Collections.sort(result);
            sites.add(new Website(website.getUrl(), website.getTitle(), result));
        }
    }

    /**
     *
     * @param query Input string. Depending on the implementation it might allow multiple words and AND and OR statements.
     * @return newList with Website objects that contains query word.
     */
    @Override
    public List<Website> lookup(String query) {
        List<Website> newList = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        boolean contains = false;
        int count = 0;
        // Go through all websites and check if word is present
        for (Website website : sites) {
            if (website.containsWord(query)) {
                contains = true;
                newList.add(website);
                System.out.println("Query is found on '" + website.getUrl() + "'");
                count++;
            }
        }
        if (!contains) {
            System.out.println("No website contains the query word.");
        }
        System.out.println("Response time: " + (System.currentTimeMillis() - startTime) + " ms. Found websites: " + count);
        System.out.println("Please provide the next query word");
        return newList;
    }

    @Override
    public Boolean validateQuery(String query) {
        return null;
    }
}