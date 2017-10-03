import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SimpleIndex implements Index {
    private List<Website> sites;

    public SimpleIndex() {
        this.sites = new ArrayList<>();
    }

    public List<Website> getSites() {
        return sites;
    }

    @Override
    public void build(List<Website> websiteList) {
        sites = new ArrayList<>();
        for (Website website : websiteList) {
            List<String> first = website.getWords();
            ArrayList<String> result = new ArrayList<String>(new HashSet<String>(first));
            Collections.sort(result);
            sites.add(new Website(website.getUrl(), website.getTitle(), result));
        }
        /*for (Website website : websiteList) {
            Collections.sort(website.getWords());
        }*/
    }

    /*public List<Website> buildN(List<Website> websiteList) {
        List<Website> list = new ArrayList<>();
        for (Website website : websiteList) {
            List<String> first = website.getWords();
            ArrayList<String> result = new ArrayList<String>(new HashSet<String>(first));
            Collections.sort(result);
            list.add(new Website(website.getUrl(), website.getTitle(), result));
        }
        return list;
    }*/

    @Override
    public List<Website> lookup(String query) {
        // String[] qWords = query.split(" ");   // for next time when we will split query in words
        List<Website> s = new ArrayList<>();
        long startTime = System.nanoTime();
        boolean contains = false;
        int count = 0;
        // Go through all websites and check if word is present
        for (Website w : sites) {
            if (w.containsWord(query)) {
                contains = true;
                s.add(new Website(w.getUrl(), w.getTitle(), null));
            }
        }
        if (!contains) {
            System.out.println("No website contains the query word.");
            System.out.println("Response time: " + (System.nanoTime() - startTime) + " ns");
        }
        System.out.println("Response time: " + (System.currentTimeMillis() - startTime) + " ms. Found websites: " + count);
        return s;
    }

    @Override
    public Boolean validateQuery(String query) {
        return null;
    }
}
