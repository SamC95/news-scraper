package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class CivilizationVII {
  public final Update newsFeed;
  private final JsoupConnector jsoupConnector;

  public CivilizationVII(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    CivilizationVII civilizationVII = new CivilizationVII(new JsoupConnector());

    try {
      civilizationVII.getNewsFeed();

      PostBuilder.createNewsPost(civilizationVII.newsFeed);
    } catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://civilization.2k.com/en-GB/civ-vii/news";

    var doc = jsoupConnector.connect(url, "civilization.2k.com");

    Element entry = doc.selectFirst("div.bc-news-tile");

    if (entry != null) {
      var title = entry.select("h3.bc-news-tile__article-heading").text();
      this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.select("a.bc-news-tile__article-link").attr("href");
      this.newsFeed.setUrl(postLink.isEmpty() ? "No url found" : "https://civilization.2k.com" + postLink);

      var imgUrl = entry.select("img.bc-news-tile__thumb__img").attr("src");
      this.newsFeed.setImage(imgUrl.isEmpty() ? "No image found" : "https:" + imgUrl);

      var description = entry.select("p.bc-news-tile__article-summary").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.newsFeed.setDescription(
          description.isEmpty() ? "No description available" : description);
    }
  }
}
