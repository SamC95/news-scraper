package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class TheOldRepublic {
    public final Update newsFeed;
    private final JsoupConnector jsoupConnector;

    public TheOldRepublic(JsoupConnector jsoupConnector) {
        this.newsFeed = new Update();
        this.jsoupConnector = jsoupConnector;
    }

    public static void main(String[] args) {
        TheOldRepublic theOldRepublic = new TheOldRepublic(new JsoupConnector());

        try {
            theOldRepublic.getNewsFeed();

            PostBuilder.createNewsPost(theOldRepublic.newsFeed);
        }
        catch (IOException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }

    public void getNewsFeed() throws IOException {
        String url = "https://www.swtor.com/info/news";

        var doc = jsoupConnector.connect(url, "swtor.com");

        Element entry = doc.selectFirst("div.newsItem.new");

        if (entry != null) {
            var title = entry.select("h2.swtorTitle").text();
            this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

            var postLink = entry.select("span.newsDesc p a.readMore").attr("href");
            if (postLink.startsWith("/")) {
                postLink = "https://www.swtor.com" + postLink;
            }
            this.newsFeed.setUrl(postLink.isEmpty() ? "No url found" : postLink);

            var imgUrl = entry.select("span.thumb.newsThumb img").attr("src");
            if (imgUrl.startsWith("/")) {
                imgUrl = "https://www.swtor.com" + imgUrl;
            }
            this.newsFeed.setImage(imgUrl.isEmpty() ? "No image available" : imgUrl);

            var description = entry.select("span.newsDesc p").size() > 1
                    ? entry.select("span.newsDesc p").get(1).text() : "No description available";
            description = Jsoup.parse(description).text();
            description = DescriptionBuilder.truncateDescription(description, 300);
            this.newsFeed.setDescription(description.isEmpty() ? "No description available" : description);
        }
    }
}
