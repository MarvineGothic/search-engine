import java.io.File;
import java.util.*;


/**
 * Implements the Index interface using a reverse index method
 */
public class ReverseIndex implements Index {
    private Map<String, List<IndexItem>> wordMap;

    /**
     * Test to see if the method works
     * @param args (no args needed)
     */
    public static void main(String args[]){
        String dir = System.getProperty("user.dir");
        List<Website> sites = FileHelper.parseFile(dir + File.separator + "data" + File.separator + "enwiki-small.txt");

        ReverseIndex index = new ReverseIndex();
        index.build(sites);

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
        return wordMap.get(query);
    }

    @Override
    public void build(List<Website> websiteList) {
        wordMap = new HashMap<>();

        // For each word that occurs in a website if that words doesn't exist in the hash map it adds it.
        // If the word is already contained it checkes if the there already exits an IndexItem with that website and
        // word. If so it adds a word position to that IndexItem
        for (Website currentSite : websiteList){
            List<String> wordsOnSite = currentSite.getWords();
            for (int i = 0; i < wordsOnSite.size(); i++){
                // Note: i is the i'th word on the current site
                String currentWord = wordsOnSite.get(i);

                if (!wordMap.containsKey(currentWord)){
                    // If the word is not present we add it to the hash map and add the current site as a IndexItem
                    IndexItem item = new IndexItem(currentSite, currentWord, i);
                    List<IndexItem> itemList = new ArrayList<>();
                    itemList.add(item);
                    wordMap.put(currentWord, itemList);
                } else {
                    List<IndexItem> indexItems = wordMap.get(currentWord);
                    // Before we can add the site as an index item we need to check if the current site already contains
                    // an index item for this word. If so, only a new entry to that ItemIndex' word positions.
                    boolean wordInSite = false;
                    for (IndexItem testItem : indexItems){
                        if (testItem.website == currentSite) {
                            testItem.wordPositions.add(i);
                            wordInSite = true;
                            break;
                        }
                    }
                    if (!wordInSite) {
                        // The site does not have an IndexItem for this word already, so we add a new one.
                        IndexItem item = new IndexItem(currentSite, currentWord, i);
                        wordMap.get(currentWord).add(item);
                    }
                }
            }
        }
    }

    @Override
    public List<Website> lookup(String query) {
        List<IndexItem> items = lookupIndexItems(query);

        if (items == null){
            return new ArrayList<>();
        }
        List<Website> siteList = new ArrayList<>();
        for (IndexItem item : items){
            siteList.add(item.website);
        }
        return siteList;
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
