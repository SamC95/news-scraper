package com.mochi.scraper.utils;

import com.mochi.scraper.model.Update;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

/*
Since Steam's RSS feed is consistent in structure across different products,
a pre-defined class for games where we're retrieving data via the Steam News Hub RSS feed is utilised.
*/
public class SteamRSSParser {
  public static void getSteamRSSNewsFeed(String gameId, Update newsFeed, JsoupConnector jsoupConnector) throws IOException {
    String url = "https://store.steampowered.com/feeds/news/app/" + gameId + "/?cc=GB&l=english";

    var doc = jsoupConnector.connect(url, "Steam news hub RSS feed (id: " + gameId + ")");

    Element entry = doc.select("item").first();

    if (entry != null) {
      var title = entry.select("title").text();
      newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.select("link").text();
      newsFeed.setUrl(postLink.isEmpty() ? "No url found" : postLink);

      var imgUrl = entry.select("enclosure").attr("url");
      newsFeed.setImage(imgUrl.isEmpty() ? "No image found" : imgUrl);

      var description = entry.select("description").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      newsFeed.setDescription(description.isEmpty() ? "No description available" : description);
    }
    else {
      System.out.println("No entries found in Steam News RSS Feed " + gameId);
    }
  }
}
