package com.example.scraper.data;

import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WarThunder {
  public final Update newsFeed;
  private final JsoupConnector jsoupConnector;

  public WarThunder(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    WarThunder warThunder = new WarThunder(new JsoupConnector());

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
    Element entry = null;

    var doc = jsoupConnector.connect(url, "warthunder.com");

    Elements entries = doc.select("div.showcase__item.widget");

    if (entries.size() > 1) {
       entry = entries.get(1);
    }

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
