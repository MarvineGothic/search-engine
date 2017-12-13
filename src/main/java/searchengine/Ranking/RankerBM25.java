package searchengine.Ranking;

import searchengine.Indexes.Index;
import searchengine.Indexes.IndexItem;
import searchengine.Website;

import java.util.List;

public class RankerBM25 extends RankerIDF {
    private float totalAmountOfWords;

    public RankerBM25(List<Website> websiteList) {
        super(websiteList);
        for (Website site : sites) {
            this.totalAmountOfWords += site.getWords().size();
        }
    }

    @Override
    public float getScore(String word, Website website, Index index) {
        return idf(word, index) * tfPlus(word, website);
    }

    @Override
    public float getScore(IndexItem indexItem, Index index) {
        return idf(indexItem.word, index) * tfPlus(indexItem);
    }

    // TODO: 05-Dec-17: This code can be done cleaner. Perhaps completely remove
    // TODO: 05-Dec-17: getScore(String word, Website website, Index index) as a method for IRanker
    private float tfPlus(IndexItem indexItem){
        float tf = this.tf(indexItem);
        float k = 1.75f;
        float b = 0.75f;
        float dL = indexItem.website.getWords().size();
        float avdL = totalAmountOfWords / (sites.size() > 0 ? sites.size() : 1);  // division by zero Exception
        return (tf * (k + 1) / (k * (1 - b + b * dL / avdL) + tf));  // just needed this parenthesis to be correct

    }


    private float tfPlus(String word, Website website) {
        float tf = this.tf(word, website);
        float k = 1.75f;
        float b = 0.75f;
        float dL = website.getWords().size();
        float avdL = totalAmountOfWords / (sites.size() > 0 ? sites.size() : 1);  // division by zero Exception

        return (tf * (k + 1) / (k * (1 - b + b * dL / avdL) + tf));  // just needed this parenthesis to be correct
    }
}
