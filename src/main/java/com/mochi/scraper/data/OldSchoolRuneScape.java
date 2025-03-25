package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class OldSchoolRuneScape {
    public final Update newsFeed;
    private final JsoupConnector jsoupConnector;

    public OldSchoolRuneScape(JsoupConnector jsoupConnector) {
        this.newsFeed = new Update();
        this.jsoupConnector = jsoupConnector;
    }

    public static void main(String[] args) {
        OldSchoolRuneScape oldSchoolRuneScape = new OldSchoolRuneScape(new JsoupConnector());

        try {
            oldSchoolRuneScape.getNewsFeed();

            PostBuilder.createNewsPost(oldSchoolRuneScape.newsFeed);
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void getNewsFeed() throws IOException {
        String url = "https://secure.runescape.com/m=news/archive?oldschool=1";

        var doc = jsoupConnector.connect(url, "secure.runescape.com");

        Element entry = doc.selectFirst("article.news-list-article");

        if (entry != null) {
            var title = entry.select("h4.news-list-article__title").text();
            this.newsFeed.setTitle(title.isEmpty() ? "No title available": title);

            var category = entry.select("span.news-list-article__category").text();
            this.newsFeed.setCategory(category.isEmpty() ? "No category found": category);

            var postLink = entry.select("a").attr("href");
            this.newsFeed.setUrl(postLink.isEmpty() ? "No url found": postLink);

            var imgUrl = entry.select("img.news-list-article__figure-img").attr("src");
            this.newsFeed.setImage(imgUrl.isEmpty() ? "No image found": imgUrl);

            var description = entry.select("p.news-list-article__summary").text();
            description = Jsoup.parse(description).text();

            description = description.replace("\u0092", "'");
            description = description.replace("\u0085", "...");
            description = description.replace("read more", "");

            description = DescriptionBuilder.truncateDescription(description, 300);
            this.newsFeed.setDescription(description.isEmpty() ? "No description available": description);
        }
    }
}
