package searchengine.Indexes;

import java.util.TreeMap;

public class ReverseTreeMapIndex extends ReverseIndex {

    @Override
    protected void InitializeWordMap(){
        wordMap = new TreeMap<>();
    }
}