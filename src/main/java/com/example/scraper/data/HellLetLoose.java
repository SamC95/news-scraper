package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

/*
Hell Let Loose does not have a dedicated news page on its website, so Steam's news hub RSS feed is being used instead
*/
public class HellLetLoose {
  private final Update newsFeed;

  public HellLetLoose() {
    this.newsFeed = new Update();
  }

  public static void main(String[] args) throws IOException {
    HellLetLoose hellLetLoose = new HellLetLoose();

    try {
      hellLetLoose.getNewsFeed();

      PostBuilder.createNewsPost(hellLetLoose.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://store.steampowered.com/feeds/news/app/686810/?cc=GB&l=english";

    var doc = JsoupConnector.connect(url, "Steam news hub RSS feed (id: 686810)");

    Element entry = doc.select("item").first();

    if (entry != null) {
      var title = entry.select("title").text();
      this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.select("link").text();
      this.newsFeed.setUrl(postLink.isEmpty() ? "No url found" : postLink);

      var imgUrl = entry.select("enclosure").attr("url");
      this.newsFeed.setImage(imgUrl.isEmpty() ? "No image found" : imgUrl);

      var description = entry.select("description").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.newsFeed.setDescription(
          description.isEmpty() ? "No description available" : description);
    }
    else {
      System.out.println("No entries found in Steam News RSS Feed");
    }
  }
}
