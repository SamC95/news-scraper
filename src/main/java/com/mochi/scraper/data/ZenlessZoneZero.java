package com.mochi.scraper.data;

import com.microsoft.playwright.ElementHandle;
import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.PlaywrightConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.TextFormatter;

import java.io.IOException;
import java.time.LocalTime;

public class ZenlessZoneZero {
    public final Update newsFeed;
    private final PlaywrightConnector playwrightConnector;

    public ZenlessZoneZero(PlaywrightConnector playwrightConnector) {
        this.newsFeed = new Update();
        this.playwrightConnector = playwrightConnector;
    }

    public static void main (String[] args) {
        ZenlessZoneZero zenlessZoneZero = new ZenlessZoneZero(new PlaywrightConnector());

        try {
            zenlessZoneZero.getNewsFeed();

            PostBuilder.createNewsPost(zenlessZoneZero.newsFeed);
        }
        catch (IOException e) {
            System.err.printf("[%s] [ERROR] %s\n", LocalTime.now(), e.getMessage());
        }
    }

    public void getNewsFeed() throws IOException {
        String url = "https://zenless.hoyoverse.com/en-us/news";

        var page = playwrightConnector.connect(url);

        ElementHandle entry = page.querySelector("div.news-list__item");

        if (entry != null) {
            String title = entry.querySelector(".news-list__item-title").textContent();
            title = TextFormatter.removeWhiteSpace(title);
            this.newsFeed.setTitle(title);

            String imgUrl = entry.querySelector(".news-list__item-banner img").getAttribute("src");
            this.newsFeed.setImage(imgUrl);

            String newsUrl = entry.querySelector("a").getAttribute("href");
            newsUrl = "https://zenless.hoyoverse.com" + newsUrl;
            this.newsFeed.setUrl(newsUrl);

            String description = entry.querySelector(".news-list__item-desc").textContent();
            this.newsFeed.setDescription(description);

            String category = entry.querySelector(".news-tag").textContent();
            category = TextFormatter.removeWhiteSpace(category);
            this.newsFeed.setCategory(category);
        }

        playwrightConnector.close();
    }
}
