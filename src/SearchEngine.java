import java.io.File;
import java.util.List;
import java.util.Scanner;

public class SearchEngine {
    public static void main(String[] args) {

        System.out.println("Welcome to the SearchEngine!");
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

        Index index;
        System.out.println("Please select a SearchEngine type by typing a number:");
        System.out.println("1 = ReverseHashMapIndex");
        System.out.println("2 = ReverseTreeMapIndex");
        System.out.println("Otherwise: SimpleIndex");
        String inputNumber = sc.nextLine();
        if (inputNumber.equals("1"))
            index = new ReverseHashMapIndex();
        else if (inputNumber.equals("2"))
            index = new ReverseTreeMapIndex();
        else
            index = new SimpleIndex();
        index.build(sites);

        System.out.println("Please provide a query word");
        while (sc.hasNext()) {
            long startTime = System.currentTimeMillis();

            String line = sc.nextLine();
            if (!index.validateQuery(line)){
                System.out.println("Query is invalid");
                continue;
            }
            line = line.toLowerCase();
            List<Website> foundSites = index.lookup(line);
            for (Website w : foundSites) {
                System.out.println("Query is found on '" + w.getUrl() + "'");
            }

            if (foundSites.size() == 0) {
                System.out.println("No websites matches the query");
            }
            System.out.println("Response time: " + (System.currentTimeMillis() - startTime) + " ns");
            System.out.println("Please provide the next query word");
        }
    }
}
