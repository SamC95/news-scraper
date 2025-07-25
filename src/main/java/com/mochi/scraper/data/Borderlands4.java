package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Objects;

public class Borderlands4 {
  public final Update newsFeed;
  private final JsoupConnector jsoupConnector;

  public Borderlands4(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    Borderlands4 borderlands4 = new Borderlands4(new JsoupConnector());

    try {
      borderlands4.getNewsFeed();

      PostBuilder.createNewsPost(borderlands4.newsFeed);
    } catch (IOException error) {
      System.err.println(error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://borderlands.2k.com/borderlands-4/news/";

    var doc = jsoupConnector.connect(url, "borderlands.2k.com");

    Element entry = doc.selectFirst("div.bc-news-tile");

    if (entry != null) {
      var title = entry.select("h3.bc-news-tile__article-heading").text();
      this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink =
          Objects.requireNonNull(entry.selectFirst("a.bc-news-tile__article-link")).attr("href");
      this.newsFeed.setUrl(
          postLink.isEmpty() ? "No url found" : "https://borderlands.2k.com" + postLink);

      var imgUrl =
          Objects.requireNonNull(entry.selectFirst("img.bc-news-tile__thumb__img")).attr("src");
      this.newsFeed.setImage(imgUrl.isEmpty() ? "No url found" : "https:" + imgUrl);

      var description = entry.select("p.bc-news-tile__article-summary").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.newsFeed.setDescription(
          description.isEmpty() ? "No description available" : description);
    }
  }
}
