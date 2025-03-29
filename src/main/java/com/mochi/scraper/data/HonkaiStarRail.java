package com.mochi.scraper.data;

import com.microsoft.playwright.ElementHandle;
import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.utils.PlaywrightConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.TextFormatter;
import org.jsoup.Jsoup;

import java.time.LocalTime;

public class HonkaiStarRail {
    public final Update newsFeed;
    private final PlaywrightConnector playwrightConnector;

    public HonkaiStarRail(PlaywrightConnector playwrightConnector) {
        this.newsFeed = new Update();
        this.playwrightConnector = playwrightConnector;
    }

    public static void main(String[] args) {
        HonkaiStarRail honkaiStarRail = new HonkaiStarRail(new PlaywrightConnector());

        try {
            honkaiStarRail.getNewsFeed();

            PostBuilder.createNewsPost(honkaiStarRail.newsFeed);
        }
        catch (Exception e) {
            System.err.printf("[%s] [ERROR] %s\n", LocalTime.now(), e.getMessage());
        }
    }

    public void getNewsFeed() {
        String url = "https://hsr.hoyoverse.com/en-us/news";

        var page = playwrightConnector.connect(url);

        ElementHandle entry = page.querySelector("a[href*='/en-us/news/']");

        if (entry != null) {
            String title = entry.querySelector(".news-title").textContent();
            title = TextFormatter.removeWhiteSpace(title);
            this.newsFeed.setTitle(title);

            String imgUrl = entry.querySelector(".img img").getAttribute("src");
            this.newsFeed.setImage(imgUrl != null ? imgUrl : "No image found");

            String newsUrl = entry.getAttribute("href");
            newsUrl = "https://hsr.hoyoverse.com" + newsUrl;
            this.newsFeed.setUrl(newsUrl);

            String description = entry.querySelector(".content").textContent();
            description = Jsoup.parse(description).text();
            description = DescriptionBuilder.truncateDescription(description, 300);
            this.newsFeed.setDescription(description);
        }
        else {
            System.err.printf("[%s] [ERROR] No entry found for Honkai Star Rail\n", LocalTime.now());
        }

        playwrightConnector.close();
    }
}
