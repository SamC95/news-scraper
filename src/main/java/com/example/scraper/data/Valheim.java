package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Valheim {
  public final Update newsFeed;
  private final JsoupConnector jsoupConnector;

  public Valheim(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    Valheim valheim = new Valheim(new JsoupConnector());

    try {
      valheim.getNewsFeed();

      PostBuilder.createNewsPost(valheim.newsFeed);
    } catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://www.valheimgame.com/news/";

    var doc = jsoupConnector.connect(url, "valheimgame.com");

    Element entry = doc.selectFirst("div.container a.news-featured");

    if (entry != null) {
      var title =
          entry.select("h1.title").text();
      this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.attr("href");
      String link = postLink.isEmpty() ? "https://www.valheimgame.com/news/" : "https://www.valheimgame.com" + postLink;
      this.newsFeed.setUrl(link);

      var imgUrl = entry.select("div.image-wrapper img").attr("src");
      this.newsFeed.setImage(imgUrl.isEmpty() ? "No image available" : imgUrl);

      var description = entry.select("div.text-content").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.newsFeed.setDescription(
          description.isEmpty() ? "No description available" : description);
    }
  }
}
