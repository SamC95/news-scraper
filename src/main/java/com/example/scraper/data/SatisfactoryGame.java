package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.PostBuilder;
import com.example.scraper.utils.SteamRSSParser;
import java.io.IOException;

/*
Satisfactory does not have a dedicated news page on its website, so Steam's news hub RSS feed is being used instead
*/
public class SatisfactoryGame {
  private final Update newsFeed;

  public SatisfactoryGame() {
    this.newsFeed = new Update();
  }

  public static void main(String[] args) {
    SatisfactoryGame satisfactoryGame = new SatisfactoryGame();

    try {
      SteamRSSParser.getSteamRSSNewsFeed("526870", satisfactoryGame.newsFeed);

      PostBuilder.createNewsPost(satisfactoryGame.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }
}
