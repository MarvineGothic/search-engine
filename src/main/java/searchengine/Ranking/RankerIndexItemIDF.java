package searchengine.Ranking;

import searchengine.Indexes.Index;
import searchengine.Indexes.IndexItem;
import searchengine.Website;

import java.util.List;

public class RankerIndexItemIDF extends RankerIDF {

    public RankerIndexItemIDF(List<Website> websiteList) {
        super(websiteList);
    }

}
