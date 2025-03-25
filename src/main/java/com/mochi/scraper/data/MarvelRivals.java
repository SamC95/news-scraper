package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MarvelRivals {
  public final Update announcementFeed;
  public final Update devDiaryFeed;
  public final Update updateFeed;
  private final JsoupConnector jsoupConnector;

  public MarvelRivals(JsoupConnector jsoupConnector) {
    this.announcementFeed = new Update();
    this.devDiaryFeed = new Update();
    this.updateFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    MarvelRivals marvelRivals = new MarvelRivals(new JsoupConnector());

    try {
      marvelRivals.getFeed("https://www.marvelrivals.com/news/", marvelRivals.announcementFeed);
      PostBuilder.createNewsPost(marvelRivals.announcementFeed);

      marvelRivals.getFeed("https://www.marvelrivals.com/devdiaries/", marvelRivals.devDiaryFeed);
      PostBuilder.createNewsPost(marvelRivals.devDiaryFeed);

      marvelRivals.getFeed("https://www.marvelrivals.com/gameupdate/", marvelRivals.updateFeed);
      PostBuilder.createNewsPost(marvelRivals.updateFeed);
    }
    catch (IOException error) {
      System.err.println("Scraper Error: " + error.getMessage());
    }
  }

  public void getFeed(String url, Update feed) throws IOException {
    var doc = jsoupConnector.connect(url, "marvelrivals.com");

    Element entry = doc.selectFirst("a.list-item");

    if (entry != null) {
      var title = entry.select("div.text h2").text();
      feed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.attr("href");
      feed.setUrl(postLink.isEmpty() ? "No URL found" : postLink);

      var imgUrl = entry.select(".img img").attr("src");
      feed.setImage(imgUrl.isEmpty() ? "No image found" : imgUrl);

      var description = entry.select("div.text p").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      feed.setDescription(
          description.isEmpty() ? "No description available" : description);
    }
  }
}
