package searchengine.Indexes;

import java.util.TreeMap;

public class InvertedTreeMapIndex extends InvertedIndex {

    @Override
    protected void InitializeWordMap(){
        wordMap = new TreeMap<>();
    }
}