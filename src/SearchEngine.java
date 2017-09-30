import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchEngine {
    public static void main(String[] args) {
        System.out.println("Welcome to the SearchEngine!");

        if (args.length != 1) {
            System.out.println("Error: Filename is missing");
            return;
        }
        Scanner sc = new Scanner(System.in);
        List<Website> sites = FileHelper.parseFile(args[0]);

        System.out.println("These are some of the available sites");
        for (int i = 0; i < Math.min(10, sites.size()); i++) {
            System.out.println(sites.get(i));
        }

        System.out.println("Please provide a query word");

        while (sc.hasNext()) {
            String line = sc.nextLine();
            // Go through all websites and check if word is present
            for (Website w : sites) {
                if (w.containsWord(line)) {
                    System.out.println("Query is found on '" + w.getUrl() + "'");
                }
                /**
                 * @author Sergiy
                 */
                else System.out.println("No website contains the query word.");
                // --------------------------------------------------------
            }
            System.out.println("Please provide the next query word");
        }
    }
}
