package com.example.scraper.data;

import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WarThunder {
  private final Update newsFeed;

  public WarThunder() {
    this.newsFeed = new Update();
  }

  public static void main(String[] args) {
    WarThunder warThunder = new WarThunder();

    try {
      warThunder.getNewsFeed();

      PostBuilder.createNewsPost(warThunder.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://warthunder.com/en/news";

    var doc = JsoupConnector.connect(url, "warthunder.com");

    Element entry = doc.selectFirst("div.showcase__item.widget");

    if (entry != null) {
      var title = entry.select("div.widget__title").text();
      this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.select("a.widget__link").attr("href");
      this.newsFeed.setUrl(postLink.isEmpty() ? "No url found" : "https://warthunder.com" + postLink);

      var widgetDiv = entry.selectFirst("div.widget__poster");
      if (widgetDiv == null) {
        this.newsFeed.setImage("No image found");
        return;
      }

      var imgPath = widgetDiv.selectFirst("img.widget__poster-media");
      if (imgPath == null) {
        this.newsFeed.setImage("No image found");
        return;
      }

      var imgUrl = imgPath.attr("data-src");
      if (imgUrl.isEmpty()) {
        imgUrl = imgPath.attr("src"); // If data-src is empty, try to fall back on src
      }

      if (imgUrl.startsWith("//")) {
        imgUrl = "https:" + imgUrl;
      }

      this.newsFeed.setImage(imgUrl.isEmpty() ? "No image found" : imgUrl);

      var description = entry.select("div.widget__comment").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.newsFeed.setDescription(
          description.isEmpty() ? "No description available" : description);
    }
    else {
      System.out.println("No entries found on warthunder.com");
    }
  }
}
