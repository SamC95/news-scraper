package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WorldOfWarcraft {
  private final Update newsFeed;

  public WorldOfWarcraft() {
    this.newsFeed = new Update();
  }

  public static void main(String[] args) {
    WorldOfWarcraft worldOfWarcraft = new WorldOfWarcraft();

    try {
      worldOfWarcraft.getNewsFeed();

      PostBuilder.createNewsPost(worldOfWarcraft.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://worldofwarcraft.blizzard.com/en-gb/news";

    var doc = JsoupConnector.connect(url, "worldofwarcraft.blizzard.com");

    Element entry = doc.selectFirst("div.List-item");

    if (entry != null) {
      var title = entry.select("div.NewsBlog-title").text();
      this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.select("a.Link.NewsBlog-link").attr("href");
      this.newsFeed.setUrl(
              postLink.isEmpty()
                      ? "No url found"
                      : "https://worldofwarcraft.blizzard.com/en-gb" + postLink);

      var imgUrl = entry.select("img.NewsBlog-image").attr("data-src");
      this.newsFeed.setImage(imgUrl.isEmpty() ? "No image found" : "https:" + imgUrl);

      var description = entry.select("p.NewsBlog-desc").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.newsFeed.setDescription(description.isEmpty() ? "No description available" : description);
    }
  }
}