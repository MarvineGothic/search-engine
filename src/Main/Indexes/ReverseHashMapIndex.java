package Main.Indexes;

import java.util.HashMap;

public class ReverseHashMapIndex extends ReverseIndex {

    @Override
    void InitializeWordMap(){
        wordMap = new HashMap<>();
    }
}
