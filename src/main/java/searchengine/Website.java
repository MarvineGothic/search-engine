package searchengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Website implements Comparable<Website>{
    private String title;
    private String url;
    private List<String> words;
    private HashSet<String> setOfWords;

    public Website(String url, String title, List<String> words) {
        this.url = url;
        this.title = title;
        this.words = words;
        setOfWords = new HashSet<>(words);
    }

    public List<String> getWords() {
        return words;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Boolean containsWord(String word) {
        return setOfWords.contains(word);
    }

    /**
     * Author Rasmus F
     */
    @Override
    public String toString(){
        String output = "Title: " +  title + "\n";
        output += "url: " +  url + "\n";
        output += "words: " +  String.join("; ", words) + "\n";
        return output;
    }

    /**
     * Author: Rasmus F
     * Get a list of what where the word occurs on the website.
     * @param word The word to check for.
     * @return A list of positions, where the positions is defined as the number of other words that occurs on the
     * website before the given word.
     */
    public List<Integer> getWordPositions(String word){
        List<Integer> wordPositions = new ArrayList<>();
        for (int i = 0; i < words.size(); i++){
            if (words.get(i).equals(word)){
                wordPositions.add(i);
            }
        }
        return wordPositions;
    }

    @Override
    public int compareTo(Website o) {
        return url.compareTo(o.url);
    }
}
