package searchengine;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import searchengine.Indexes.IRanker;
import searchengine.Indexes.Index;
import searchengine.Indexes.RankerBM25;
import searchengine.Indexes.SimpleIndex;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;


/**
 * The main class of our search engine program.
 *
 * @author Martin Aumüller
 * @author Leonid Rusnac
 */
@Configuration
@EnableAutoConfiguration
@Path("/")
public class SearchEngine extends ResourceConfig {
    private static Index currentIndex; // TODO: 07-Nov-17 Should be improved to allow multiple instances???
    private static IRanker currentRanker; // TODO: 07-Nov-17 Should be improved to allow multiple instances???

    public SearchEngine() {
        packages("searchengine");
    }


    /**
     * The main method of our search engine program.
     * Expects exactly one argument being provided. This
     * argument is the filename of the file containing the
     * websites.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the Search Engine!");

        if (args.length != 1) {
            System.out.println("Error: Filename is missing");
            return;
        }

        currentIndex = new SimpleIndex();
        long t1 = System.nanoTime();
        List<Website> sites = FileHelper.loadFile(args[0]);
        currentIndex.build(sites);
        currentRanker = new RankerBM25(sites);
        long t2 = System.nanoTime();
        System.out.println("Processing the data set and building the currentIndex took " +
                (t2 - t1) / 10e6 + " milliseconds.");
        // run the search engine
        SpringApplication.run(SearchEngine.class);
    }

    /**
     * This methods handles requests to GET requests at search.
     * It assumes that a GET request of the form "search?query=word" is made.
     *
     * @param response Http response object
     * @param query the query string
     * @return the list of websites matching the query
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("search")
    public List<Website> search(@Context HttpServletResponse response, @QueryParam("query") String query) {
        // Set cross domain access. Otherwise your browser will complain that it does not want
        // to load code from a different location.
        response.setHeader("Access-Control-Allow-Origin", "*");


        if (query == null) {
            return new ArrayList<Website>();
        }

        String line = query;

        System.out.println("Handling request for query word \"" + query + "\"");

        List<Website> resultList = IndexMethods.multiWordQuery(currentIndex, line, currentRanker);
        System.out.println("Found " + resultList.size() + " websites.");
        return resultList;
    }
}