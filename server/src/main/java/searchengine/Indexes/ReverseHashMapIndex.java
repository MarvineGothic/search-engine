package searchengine.Indexes;

import java.util.HashMap;

public class ReverseHashMapIndex extends ReverseIndex {
    @Override
    protected void InitializeWordMap(){
        wordMap = new HashMap<>();
    }
}
