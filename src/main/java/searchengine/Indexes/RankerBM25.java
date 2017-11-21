package searchengine.Indexes;

import searchengine.Website;

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
    public float getScore(String word, Website website, Index index) {
        return idf(word, index) * tfPlus(word, website);            
    }

    private float tfPlus(String word, Website website) {
        float tf = this.tf(word, website);
        float k = 1.75f;
        float b = 0.75f;
        int dL = website.getWords().size();
        int avdL = totalAmountOfWords / (sites.size() > 0 ? sites.size() : 1);  // division by zero Exception

        return (tf * (k + 1) / (k * (1 - b + b * dL / avdL) + tf));  // just needed this parenthesis to be correct
    }
}
