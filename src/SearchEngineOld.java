import java.io.File;
import java.util.List;
import java.util.Scanner;

public class SearchEngineOld {
    public static void main(String[] args) {

        System.out.println("Welcome to the SearchEngine!");
        if (args.length <=0) {
            System.out.println("Error: Filename is missing");
            return;
        }
        Scanner sc = new Scanner(System.in);

        // so we don't need the directory path:
        // Now you only need to specify the relative path from the project folder to the datafile
        String dir = System.getProperty("user.dir");
        List<Website> sites = FileHelper.parseFile(dir + File.separator + "data" + File.separator + args[0]);
        // ----------------------------------------------------------------
        
        System.out.println("These are some of the available sites");
        for (int i = 0; i < Math.min(10, sites.size()); i++) {
            System.out.println(sites.get(i));
        }

        System.out.println("Please provide a query word");

        /**
         * @author Sergiy
         */
        while (sc.hasNext()) {
            long startTime = System.nanoTime();
            boolean contains = false;
            String line = sc.nextLine();
            // Go through all websites and check if word is present
            for (Website w : sites) {
                if (w.containsWord(line)) {
                    contains = true;
                    System.out.println("Query is found on '" + w.getUrl() + "'");
                    System.out.println("Response time: " + (System.nanoTime() - startTime) + " ns");
                }
            }
            if (!contains) {
                System.out.println("No website contains the query word.");
                System.out.println("Response time: " + (System.nanoTime() - startTime) + " ns");
            }
            System.out.println("Please provide the next query word");
        }
    }
}
