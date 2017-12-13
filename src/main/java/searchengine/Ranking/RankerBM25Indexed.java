package searchengine.Ranking;

import searchengine.IndexedWebsite;
import searchengine.Indexes.Index;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankerBM25Indexed extends RankerBM25 {

    public RankerBM25Indexed(List<Website> websiteList) {
        super(websiteList);
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idfUsingIndex(word, index, website) * tfPlusUsingIndex(word, website);
    }
}
