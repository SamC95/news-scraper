package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.PostBuilder;
import com.example.scraper.utils.SteamRSSParser;

import java.io.IOException;

/*
Hell Let Loose does not have a dedicated news page on its website, so Steam's news hub RSS feed is being used instead
*/
public class HellLetLoose {
  private final Update newsFeed;

  public HellLetLoose() {
    this.newsFeed = new Update();
  }

  public static void main(String[] args) {
    HellLetLoose hellLetLoose = new HellLetLoose();

    try {
        SteamRSSParser.getSteamRSSNewsFeed("686810", hellLetLoose.newsFeed);

        PostBuilder.createNewsPost(hellLetLoose.newsFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }
}