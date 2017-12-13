package searchengine.Ranking;

import searchengine.Indexes.Index;
import searchengine.Indexes.IndexItem;
import searchengine.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankerIDF implements IRanker {
    protected List<Website> sites = new ArrayList<>();

    public RankerIDF(List<Website> websiteList) {
        sites = websiteList;
    }

    public float getScore(IndexItem indexItem, Index index) {
        return idf(indexItem.word, index) * tf(indexItem);
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idf(word, index) * tf(word, website);
    }

    public float tf(String word, Website website) {
        return Collections.frequency(website.getWords(), word);
    }

    public float tf(IndexItem indexItem) {
        return indexItem.getWordCount();
    }

    public float idf(String word, Index index) {
        float d = sites.size();
        float n = index.lookup(word).size();
        return (float) (Math.log(d / n) / Math.log(2));
    }


}


