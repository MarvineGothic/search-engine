package searchengine.Indexes;

import searchengine.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankerIDF implements IRanker {
    protected List<Website> sites = new ArrayList<>();

    public RankerIDF(List<Website> websiteList) {
        sites = websiteList;
    }

    @Override
    public double getScore(String word, Website website, Index index) {
        return idf(word, index) * tf(word, website);
    }

    public double tf(String word, Website website) {
        return Collections.frequency(website.getWords(), word);
    }

    public double idf(String word, Index index) {
        double d = sites.size();
        double n = index.lookup(word).size();

        return (Math.log(d / n) / Math.log(2));
    }


}


