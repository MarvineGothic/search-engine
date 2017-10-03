import java.io.File;
import java.util.*;


/**
 * Implements the Index interface using a reverse index method
 */
public class ReverseIndex implements Index {
    private Map<String, HashSet<Website>> wordMap;

    /**
     * Test to see if the method works
     * @param args (no args needed)
     */
    public static void main(String args[]){
        String dir = System.getProperty("user.dir");
        List<Website> sites = FileHelper.parseFile(dir + File.separator + "data" + File.separator + "enwiki-medium.txt");

        ReverseIndex index = new ReverseIndex();
        index.build(sites);

        System.out.println(index.lookup("modern").size());
        List<IndexItem> result = index.lookupIndexItems("district");
        for (IndexItem item : result){
            System.out.println(item);
        }
    }

    /**
     * This method returns information about all websites matching the query.
     * It contain information about where the words occurs in the website and how many times.
     * @param query A single word. The word should be valid according to the "validateQuery" method.
     * @return All websites matching the query in the form of IndexItems
     */
    public List<IndexItem> lookupIndexItems(String query){
        List<IndexItem> itemList = new ArrayList<>();
        for (Website site : lookup(query)){
            List<Integer> wordPositions = new ArrayList<>();
            for (int i = 0; i < site.getWords().size(); i++){
                if (site.getWords().get(i).equals(query)){
                    wordPositions.add(i);
                }
            }
            itemList.add(new IndexItem(site, query, wordPositions));
        }
        return itemList;
    }

    @Override
    public void build(List<Website> websiteList) {
        wordMap = new HashMap<>();
        for (Website currentSite : websiteList){
            for (String wordOnSite : currentSite.getWords()){
                wordMap.computeIfAbsent(wordOnSite, key -> new HashSet<>());
                wordMap.get(wordOnSite).add(currentSite);
            }
        }
    }

    @Override
    public List<Website> lookup(String query) {
        HashSet<Website> sites = wordMap.getOrDefault(query, new HashSet<>());
        return new ArrayList<>(sites);
    }

    /**
     * A valid query is a string that only contains numeric symbols 0-9 or letters a-z (both small and large letters).
     * @param query The query to test
     * @return True if query is valid
     */
    @Override
    public Boolean validateQuery(String query) {
        // Checks if the query contains any non standard letters and numbers
        String strppedWord = query.replaceAll("[^a-zA-Z0-9]", "");
        return strppedWord.length() >= query.length();
    }
}
