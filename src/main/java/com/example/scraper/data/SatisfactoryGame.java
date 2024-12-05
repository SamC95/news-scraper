package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.JsoupConnector;
import com.example.scraper.utils.PostBuilder;
import com.example.scraper.utils.SteamRSSParser;
import java.io.IOException;

/*
Satisfactory does not have a dedicated news page on its website, so Steam's news hub RSS feed is being used instead
*/
public class SatisfactoryGame {
  public final Update newsFeed;
  private final JsoupConnector jsoupConnector;

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
