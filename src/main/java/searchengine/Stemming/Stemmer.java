package searchengine.Stemming;

/**
 * <pre>
 * This class can stem a text file encoded with utf-8 encoding
 * </pre>
 */
public class Stemmer {

    /**
     * <pre>
     * This method stems a single word. That is it reduces the word to the word to the base of the word
     * (fx "scattered" becomes "scatter").
     * Note: This method is far from perfect and sometimes reduces similar words with different meaning to the same base
     * For a better description read https://www.elastic.co/guide/en/elasticsearch/guide/current/stemming.html
     *
     * @param word The word to be stemmed
     * @return The stemmed word
     * </pre>
     */
    public static String StemWord(String word) {
        PortersStemmer stemmer = new PortersStemmer();
        stemmer.add(word.toCharArray(), word.length());
        stemmer.stem();
        return stemmer.toString();
    }
}
