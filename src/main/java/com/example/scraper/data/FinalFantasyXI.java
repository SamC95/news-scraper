package com.example.scraper.data;

import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalTime;

public class FinalFantasyXI {
    public final Update topicFeed;
    public final Update informationFeed;
    private final JsoupConnector jsoupConnector;

    public FinalFantasyXI(JsoupConnector jsoupConnector) {
        this.topicFeed = new Update();
        this.informationFeed = new Update();
        this.jsoupConnector = jsoupConnector;
    }

    public static void main(String[] args) {
        FinalFantasyXI finalFantasyXI = new FinalFantasyXI(new JsoupConnector());

        try {
            finalFantasyXI.getTopicFeed();
            finalFantasyXI.getInformationFeed();

            PostBuilder.createNewsPost(finalFantasyXI.topicFeed);
            PostBuilder.createNewsPost(finalFantasyXI.informationFeed);
        }
        catch (IOException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }

    public void getTopicFeed() throws IOException {
        String url = "http://www.playonline.com/pcd/topics/ff11eu/topics.xml";

        var doc = jsoupConnector.connect(url, "PlayOnline RSS Feed");

        Element entry = doc.select("item").first();

        if (entry != null && !entry.text().isEmpty()) {
            var title = entry.select("title").text();
            this.topicFeed.setTitle(title.isEmpty() ? "No title available" : title);

            var postLink = entry.select("link").text();
            this.topicFeed.setUrl(postLink.isEmpty() ? "No url found" : postLink);

            var description = entry.select("description").text();
            description = Jsoup.parse(description).text();
            description = DescriptionBuilder.truncateDescription(description, 300);
            this.topicFeed.setDescription(description.isEmpty() ? "No description available" : description);
        }

        url = "http://www.playonline.com/ff11eu/index.shtml";

        doc = jsoupConnector.connect(url, "PlayOnline RSS Feed");

        Element mostRecentUpdate = doc.selectFirst("p.tx_topics > a > img");

        if (mostRecentUpdate != null) {
            String imgUrl = mostRecentUpdate.attr("src");
            this.topicFeed.setImage("http://www.playonline.com" + imgUrl);
        }
    }

    public void getInformationFeed() throws IOException {
        String url = "http://www.playonline.com/ff11eu/polnews/news.xml";

        var doc = jsoupConnector.connect(url, "PlayOnline RSS Feed");

        Element entry = doc.select("item").first();

        if (entry != null) {
            var title = entry.select("title").text();
            this.informationFeed.setTitle(title.isEmpty() ? "No title available" : title);

            var postLink = entry.select("link").text();
            this.informationFeed.setUrl(postLink.isEmpty() ? "No url found" : postLink);

            var description = entry.select("description").text();
            description = Jsoup.parse(description).text();
            description = DescriptionBuilder.truncateDescription(description, 300);
            this.informationFeed.setDescription(description.isEmpty() ? "No description available" : description);
        }
        else {
            System.out.printf("[%s] [INFO] No entries found in PlayOnline RSS Feed", LocalTime.now());
        }
    }
}
