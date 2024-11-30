package com.example.scraper.games;

import com.example.scraper.data.DescriptionBuilder;
import com.example.scraper.data.Patch;
import com.example.scraper.data.PostBuilder;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Overwatch {
  private final Patch patch;
  private final String thumbnail = "https://i.imgur.com/NDhNeBj.png";

  public Overwatch() {
    this.patch = new Patch();
  }

  public static void main(String[] args) {
    Overwatch overwatch = new Overwatch();

    try {
      overwatch.getNewsFeed();
      PostBuilder.createNewsPost(overwatch.patch);
    } catch (Exception error) {
      System.out.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed() throws IOException {
    String url = "https://playoverwatch.com/en-us/news/patch-notes/pc/";
    Document doc;

    try {
      doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
    } catch (IOException error) {
      throw new IOException("Couldn't connect to https://playoverwatch.com");
    }

    try {
      Element mostRecentUpdate = doc.selectFirst("div.PatchNotes-patch.PatchNotes-live");

      if (mostRecentUpdate != null) {
        Element title = doc.selectFirst("h3.PatchNotes-patchTitle");
          assert title != null;
          this.patch.setTitle(title.text());

        Element pageLink = doc.selectFirst("div.PatchNotesTop blz-button");
        assert pageLink != null;
        this.patch.setUrl(
            "https://overwatch.blizzard.com/en-us/news/patch-notes/live/" + pageLink.attr("href"));

        Element patchDescription = mostRecentUpdate.selectFirst("div.PatchNotes-sectionDescription");

        new DescriptionBuilder(patchDescription, patch);
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
