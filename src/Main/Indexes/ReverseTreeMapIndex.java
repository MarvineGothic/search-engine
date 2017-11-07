package Main.Indexes;

import java.util.TreeMap;

public class ReverseTreeMapIndex extends ReverseIndex {

    @Override
    public void InitializeWordMap(){
        wordMap = new TreeMap<>();
    }
}