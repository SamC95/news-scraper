package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Objects;

public class RunescapeDragonwilds {
  public final Update newsFeed;
  private final JsoupConnector jsoupConnector;

  public RunescapeDragonwilds(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    RunescapeDragonwilds runescapeDragonwilds = new RunescapeDragonwilds(new JsoupConnector());

    try {
      runescapeDragonwilds.getNewsFeed();

      PostBuilder.createNewsPost(runescapeDragonwilds.newsFeed);
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://dragonwilds.runescape.com/news";

    var doc = jsoupConnector.connect(url, "dragonwilds.runescape.com");

    Element entry = doc.selectFirst("a[data-framer-name=Default][href^=./news/]:not([href=./news])");

    if (entry != null) {
      String postLink = entry.attr("href");

      if (postLink.startsWith("./"))
        postLink = postLink.substring(1);

      this.newsFeed.setUrl("https://dragonwilds.runescape.com" + postLink);

      String title =
          Objects.requireNonNull(entry.selectFirst("div[data-framer-name='Article title'] h2")).text();
      this.newsFeed.setTitle(title);

      String description =
          Objects.requireNonNull(entry.selectFirst("div[data-framer-name='Snippet'] p")).text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.newsFeed.setDescription(description);

      String imgUrl =
          Objects.requireNonNull(entry.selectFirst("div[data-framer-background-image-wrapper] img"))
              .attr("src");
      this.newsFeed.setImage(imgUrl);
    }
  }
}
