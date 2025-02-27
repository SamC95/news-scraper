package com.example.scraper.data;

import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalTime;

public class Overwatch {
  public final Update patchFeed;
  public final Update newsFeed;
  private final JsoupConnector jsoupConnector;

  public Overwatch(JsoupConnector jsoupConnector) {
    this.patchFeed = new Update();
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    Overwatch overwatch = new Overwatch(new JsoupConnector());

    try {
      overwatch.getPatchNotes();
      overwatch.getNewsFeed();

      PostBuilder.createNewsPost(overwatch.patchFeed);
      PostBuilder.createNewsPost(overwatch.newsFeed);
    } catch (Exception error) {
      System.out.println("Error: " + error.getMessage());
    }
  }

  public void getPatchNotes() throws IOException {
    String url = "https://playoverwatch.com/en-us/news/patch-notes/pc/";

    var doc = jsoupConnector.connect(url, "overwatch.blizzard.com");

    Element entry = doc.selectFirst("div.PatchNotes-patch.PatchNotes-live");

    if (entry != null) {
      var titleElement = doc.selectFirst("h3.PatchNotes-patchTitle");
      String title = (titleElement != null && !titleElement.text().isEmpty()) ? titleElement.text() : "No title available";
      this.patchFeed.setTitle(title);

      var postLink = doc.select("div.PatchNotesTop blz-button").attr("href");
      this.patchFeed.setUrl(
          postLink.isEmpty()
              ? "No url found"
              : "https://overwatch.blizzard.com/en-us/news/patch-notes/live/" + postLink);

      var description = entry.select("div.PatchNotes-sectionDescription").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      this.patchFeed.setDescription(description.isEmpty() ? "No description available" : description);
    }
    else {
      System.err.printf("[%s] [INFO] No entries found in Overwatch 2 Patch Notes\n", LocalTime.now());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://overwatch.blizzard.com/en-us/news";

    var doc = jsoupConnector.connect(url, "overwatch.blizzard.com");

    Element entry = doc.selectFirst("blz-card[slot=gallery-items]");

    if (entry != null) {
      var title = entry.select("h4[slot=heading]").text();
      this.newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.attr("href");
      this.newsFeed.setUrl(postLink.isEmpty() ? "No url found" : "https://overwatch.blizzard.com" + postLink);

      var imgUrl = entry.select("blz-image[slot=image]").attr("src");
      this.newsFeed.setImage(imgUrl.isEmpty() ? "No image available" : imgUrl);
    }
    else {
      System.err.printf("[%s] [INFO] No entries found in Overwatch 2 news feed\n", LocalTime.now());
    }
  }
}
