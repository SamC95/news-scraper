package com.mochi.scraper.data;

import com.microsoft.playwright.ElementHandle;
import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.PlaywrightConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.TextFormatter;

import java.time.LocalTime;

public class WutheringWaves {
    public final Update newsFeed;
    private final PlaywrightConnector playwrightConnector;

    public WutheringWaves(PlaywrightConnector playwrightConnector) {
        this.newsFeed = new Update();
        this.playwrightConnector = playwrightConnector;
    }

    public static void main(String[] args) {
        WutheringWaves wutheringWaves = new WutheringWaves(new PlaywrightConnector());

        try {
            wutheringWaves.getNewsFeed();

            PostBuilder.createNewsPost(wutheringWaves.newsFeed);
        }
        catch (Exception e) {
            System.err.printf("[%s] [ERROR] %s\n", LocalTime.now(), e.getMessage());
        }
    }

    public void getNewsFeed() {
        String url = "https://wutheringwaves.kurogames.com/en/main#news";

        var page = playwrightConnector.connect(url);

        page.waitForSelector(".article.cur");

        ElementHandle entry = page.querySelector(".article.cur");

        if (entry != null) {
            String category = entry.querySelector(".text-type").textContent();
            this.newsFeed.setCategory(category);

            String title = entry.querySelector(".text-row p").textContent();
            title = TextFormatter.removeWhiteSpace(title);
            this.newsFeed.setTitle(title);

            entry.click();
            page.waitForTimeout(1000);
            this.newsFeed.setUrl(page.url());

            page.waitForSelector(".news-content img");
            ElementHandle firstImage = page.querySelector(".news-content img");
            String imgUrl = firstImage != null ? firstImage.getAttribute("src") : null;
            this.newsFeed.setImage(imgUrl);
        }
        else {
            System.err.printf("[%s] [ERROR] Failed to get Wuthering Waves news feed", LocalTime.now());
        }

        playwrightConnector.close();
    }
}
