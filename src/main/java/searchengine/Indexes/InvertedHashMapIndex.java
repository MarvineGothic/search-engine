package searchengine.Indexes;

import java.util.HashMap;

public class InvertedHashMapIndex extends InvertedIndex {
    @Override
    protected void InitializeWordMap(){
        wordMap = new HashMap<>();
    }
}
