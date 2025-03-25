package com.mochi.scraper.data;

import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.SteamRSSParser;

import java.io.IOException;

/*
Utilising Monster Hunter Wilds Steam news page to retrieve posts
 */
public class MonsterHunterWilds {
  public final Update newsFeed;
  public final JsoupConnector jsoupConnector;

  public MonsterHunterWilds(JsoupConnector jsoupConnector) {
    this.newsFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    MonsterHunterWilds monsterHunterWilds = new MonsterHunterWilds(new JsoupConnector());

    try {
      SteamRSSParser.getSteamRSSNewsFeed(
          "2246340", monsterHunterWilds.newsFeed, monsterHunterWilds.jsoupConnector);

      PostBuilder.createNewsPost(monsterHunterWilds.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }
}
