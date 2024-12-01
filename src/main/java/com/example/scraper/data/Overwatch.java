package com.example.scraper.data;

import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Overwatch {
  private final Update update;
  private final String thumbnail = "https://i.imgur.com/NDhNeBj.png";

  public Overwatch() {
    this.update = new Update();
  }

  public static void main(String[] args) {
    Overwatch overwatch = new Overwatch();

    try {
      overwatch.getNewsFeed();
      PostBuilder.createNewsPost(overwatch.update);
    } catch (Exception error) {
      System.out.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://playoverwatch.com/en-us/news/patch-notes/pc/";
    Document doc;

    doc = JsoupConnector.connect(url, "playoverwatch.com");

    try {
      Element mostRecentUpdate = doc.selectFirst("div.PatchNotes-patch.PatchNotes-live");

      if (mostRecentUpdate != null) {
        Element title = doc.selectFirst("h3.PatchNotes-patchTitle");
          assert title != null;
          this.update.setTitle(title.text());

        Element pageLink = doc.selectFirst("div.PatchNotesTop blz-button");
        assert pageLink != null;
        this.update.setUrl(
            "https://overwatch.blizzard.com/en-us/news/patch-notes/live/" + pageLink.attr("href"));

        Element patchDescription = mostRecentUpdate.selectFirst("div.PatchNotes-sectionDescription");

        new DescriptionBuilder(patchDescription, update);
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
