package Main.Indexes;

import Main.Website;

import java.util.Collections;
import java.util.List;

public class RankerBM25 extends RankerIDF {
    private int totalAmountOfWords;

    public RankerBM25(List<Website> websiteList) {
        super(websiteList);
        for (Website site : sites) {
            this.totalAmountOfWords += site.getWords().size();
        }
    }
        // I added tfplus instead of tf (by the formula from slides)
        // so this method becomes actually bm25
    @Override
    public long getScore(String word, Website website, Index index) {
        return idf(word, index) * tfPlus(word, website);            
    }

    private long tfPlus(String word, Website website) {
        long tf = this.tf(word, website);
        double k = 1.75;
        double b = 0.75;
        int dL = website.getWords().size();
        int avdL = totalAmountOfWords / sites.size();

        return (long) (tf * (k + 1) / (k * (1 - b + b * dL / avdL) + tf));  // just needed this parenthesis to be correct
    }
}
