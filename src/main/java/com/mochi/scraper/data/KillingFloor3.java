package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.SteamRSSParser;

import java.io.IOException;

// Posts retrieved via Steam news page
public class KillingFloor3 {
  public final Update newsFeed;
  public final JsoupConnector jsoupConnector;

  public KillingFloor3(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    KillingFloor3 killingFloor3 = new KillingFloor3(new JsoupConnector());

    try {
      SteamRSSParser.getSteamRSSNewsFeed(
          "1430190", killingFloor3.newsFeed, killingFloor3.jsoupConnector);

      PostBuilder.createNewsPost(killingFloor3.newsFeed);
    } catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }
}
