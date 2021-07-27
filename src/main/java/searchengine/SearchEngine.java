package searchengine;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import searchengine.Indexes.Index;
import searchengine.Indexes.InvertedHashMapIndex;
import searchengine.Ranking.BM25Score;
import searchengine.Ranking.Score;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * The main class of the search engine program, through which communication with the graphical web interface
 * is run, and input arguments are received.
 * </pre>
 */
@Configuration
@EnableAutoConfiguration
@Path("/")
public class SearchEngine extends ResourceConfig {
    private static Index currentIndex;
    private static Score currentRanker;

    public SearchEngine() {
        packages("searchengine");
    }

    /**
     * <pre>
     * The main method of our search engine program. Expects exactly one argument being provided.
     * This argument is the filename of a database .txt file containing website elements.
     *
     * @param args command line arguments. The name of a .txt file containing website data.
     *            The file must be placed in the data folder of the project.
     * </pre>
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the Search Engine!");

        if (args.length != 1) {
            System.out.println("Error: Filename is missing or has an incorrect format");
            return;
        }

        currentIndex = new InvertedHashMapIndex();
        long t1 = System.nanoTime();
        long m1 = System.currentTimeMillis();
        List<Website> sites = FileHelper.loadFile(args[0]);
        currentIndex.build(sites);
        currentRanker = new BM25Score(sites);
        long t2 = System.nanoTime();
        long m2 = System.currentTimeMillis();
        System.out.println("Processing the data set and building the currentIndex took " +
                (t2 - t1) / 10e6 + " milliseconds.");
        System.out.println("Processing the data set and building the currentIndex took " +
                (m2 - m1) + " milliseconds.");

        SpringApplication.run(SearchEngine.class);
    }

    /**
     * <pre>
     * This methods handles requests to GET at search.
     * It assumes that a GET request of the form "search?query=word" is made.
     * The response header is set to allow cross domain access
     *
     * @param response Http response object
     * @param query    the query string
     * @return the list of websites matching the query
     * </pre>
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("search")
    public List<Website> search(@Context HttpServletResponse response, @QueryParam("query") String query) {
        long startTime = System.nanoTime();
        response.setHeader("Access-Control-Allow-Origin", "*");

        if (query == null) {
            return new ArrayList<>();
        }

        System.out.println("Handling request for query word \"" + query + "\"");

        List<Website> resultList = QueryHandler.multiWordQuery(currentIndex, query, currentRanker);
        long endTime = System.nanoTime();
        System.out.println("Found " + resultList.size() + " websites in " + (endTime - startTime) / 10e6 + " milliseconds.");

        if (resultList.isEmpty()) System.out.println("No website contains the query word.");

        return resultList;
    }
}