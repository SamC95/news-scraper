package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.SteamRSSParser;
import java.io.IOException;

/*
Satisfactory does not have a dedicated news page on its website, so Steam's news hub RSS feed is being used instead
*/
public class SatisfactoryGame {
  public final Update newsFeed;
  public final JsoupConnector jsoupConnector;

  public SatisfactoryGame(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    SatisfactoryGame satisfactoryGame = new SatisfactoryGame(new JsoupConnector());

    try {
      SteamRSSParser.getSteamRSSNewsFeed(
          "526870", satisfactoryGame.newsFeed, satisfactoryGame.jsoupConnector);

      PostBuilder.createNewsPost(satisfactoryGame.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }
}
