package Main;

import Main.Indexes.*;

import java.util.List;
import java.util.Scanner;

public class SearchEngine {
    public static void main(String[] args) {

        System.out.println("Welcome to the Main.SearchEngine!");
        if (args.length <=0) {
            System.out.println("Error: Filename is missing");
            return;
        }
        Scanner sc = new Scanner(System.in);
        List<Website> sites = FileHelper.loadFile(args[0]);

        System.out.println("These are some of the available sites");
        for (int i = 0; i < Math.min(3, sites.size()); i++) {
            System.out.println(sites.get(i));
        }
        Index index = new ReverseHashMapIndex();
        index.build(sites);
        IRanker ranker = new RankerBM25(sites);

        System.out.println("Please provide a query word");
        while (sc.hasNext()) {
            long startTime = System.currentTimeMillis();
            String line = sc.nextLine();
            List<Website> foundSites = IndexMethods.multiWordQuery(index, line, ranker);
            for (Website w : foundSites) {
                System.out.println("Query is found on '" + w.getUrl() + "'");
            }

            if (foundSites.size() == 0) {
                System.out.println("No websites matches the query");
            }
            System.out.println("Response time: " + (System.currentTimeMillis() - startTime) + " ns\n");
            System.out.println("Please provide the next query word");
        }
    }
}
