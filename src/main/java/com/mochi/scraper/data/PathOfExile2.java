package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.SteamRSSParser;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class PathOfExile2 {
    public final Update newsFeed;
    public final Update patchFeed;
    public final JsoupConnector jsoupConnector;

    public PathOfExile2(JsoupConnector jsoupConnector) {
        this.newsFeed = new Update();
        this.patchFeed = new Update();
        this.jsoupConnector = jsoupConnector;
    }

    public static void main(String[] args) {
        PathOfExile2 pathOfExile2 = new PathOfExile2(new JsoupConnector());

        try {
            SteamRSSParser.getSteamRSSNewsFeed("2694490", pathOfExile2.newsFeed, pathOfExile2.jsoupConnector);
            pathOfExile2.getPatchNotes();

            PostBuilder.createNewsPost(pathOfExile2.newsFeed);
            PostBuilder.createNewsPost(pathOfExile2.patchFeed);
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void getPatchNotes() throws IOException {
        String url = "https://pathofexile.com/forum/view-forum/2212";

        var doc = jsoupConnector.connect(url, "pathofexile.com");

        Element entry = doc.selectFirst("td.thread");

        if (entry != null) {
            var title = entry.select(".thread_title .title a").text();
            this.patchFeed.setTitle(title.isEmpty() ? "No title available" : title);

            var postLink = entry.select(".thread_title .title a").attr("href");
            this.patchFeed.setUrl(postLink.isEmpty() ? "No url found" : "https://pathofexile.com" + postLink);

            var author = entry.select(".postBy .profile-link a").text();
            this.patchFeed.setAuthor(author.isEmpty() ? "No author found" : author);

            // These posts do not have images or descriptions so we simply set them as below
            this.patchFeed.setImage("No image found");
            this.patchFeed.setDescription("No description available");
        }
    }
}
