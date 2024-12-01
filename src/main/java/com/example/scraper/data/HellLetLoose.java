package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import com.example.scraper.utils.SteamRSSParser;

import java.io.IOException;

/*
Hell Let Loose does not have a dedicated news page on its website, so Steam's news hub RSS feed is being used instead
*/
public class HellLetLoose {
  private final Update newsFeed;
  private final JsoupConnector jsoupConnector;

  public HellLetLoose(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    HellLetLoose hellLetLoose = new HellLetLoose(new JsoupConnector());

    try {
      SteamRSSParser.getSteamRSSNewsFeed(
          "686810", hellLetLoose.newsFeed, hellLetLoose.jsoupConnector);

      PostBuilder.createNewsPost(hellLetLoose.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }
}
