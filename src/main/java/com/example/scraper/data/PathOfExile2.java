package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import com.example.scraper.utils.SteamRSSParser;

import java.io.IOException;

public class PathOfExile2 {
    public final Update newsFeed;
    public final JsoupConnector jsoupConnector;

    public PathOfExile2(JsoupConnector jsoupConnector) {
        this.newsFeed = new Update();
        this.jsoupConnector = jsoupConnector;
    }

    public static void main(String[] args) {
        PathOfExile2 pathOfExile2 = new PathOfExile2(new JsoupConnector());

        try {
            SteamRSSParser.getSteamRSSNewsFeed("2694490", pathOfExile2.newsFeed, pathOfExile2.jsoupConnector);

            PostBuilder.createNewsPost(pathOfExile2.newsFeed);
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
