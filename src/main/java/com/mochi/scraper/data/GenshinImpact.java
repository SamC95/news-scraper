package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.PlaywrightConnector;
import com.microsoft.playwright.ElementHandle;

import java.io.IOException;
import java.time.LocalTime;

public class GenshinImpact {
    public final Update newsFeed;
    private final PlaywrightConnector playwrightConnector;

    public GenshinImpact(PlaywrightConnector seleniumConnector) {
        this.newsFeed = new Update();
        this.playwrightConnector = seleniumConnector;
    }

    public static void main(String[] args) {
        GenshinImpact genshinImpact = new GenshinImpact(new PlaywrightConnector());

        try {
            genshinImpact.getNewsFeed();

            PostBuilder.createNewsPost(genshinImpact.newsFeed);
        }
        catch (IOException e) {
            System.err.printf("[%s] [ERROR] %s\n", LocalTime.now(), e.getMessage());
        }
    }

    public void getNewsFeed() throws IOException {
        String url = "https://genshin.hoyoverse.com/en/news";

        var page = playwrightConnector.connect(url);

        ElementHandle entry = page.querySelector("li.news__item.news__tag-2");

        if (entry != null) {
            String title = entry.querySelector("h3").textContent();
            this.newsFeed.setTitle(title);

            String imgUrl = entry.querySelector("img.coverFit").getAttribute("src");
            this.newsFeed.setImage(imgUrl);

            String newsUrl = entry.querySelector("a.news__title").getAttribute("href");
            newsUrl = "https://genshin.hoyoverse.com" + newsUrl;
            this.newsFeed.setUrl(newsUrl);

            String description = entry.querySelector("p.news__summary").textContent();
            this.newsFeed.setDescription(description);

            String category = entry.querySelector("a.news__category").textContent();
            this.newsFeed.setCategory(category);
        }

        playwrightConnector.close();
    }
}
