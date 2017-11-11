package Main.Indexes;

import Main.Website;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankerIDF implements IRanker {
    protected List<Website> sites = new ArrayList<>();

    public RankerIDF(List<Website> websiteList) {
        sites = websiteList;
    }

    @Override
    public long getScore(String word, Website website, Index index) {
        return idf(word, index) * tf(word, website);
    }

    public long tf(String word, Website website) {
        return Collections.frequency(website.getWords(), word);
    }

    public long idf(String word, Index index) {
        double d = sites.size();
        double n = index.lookup(word).size();

        return (long) (Math.log(d / n) / Math.log(2));
    }


}


