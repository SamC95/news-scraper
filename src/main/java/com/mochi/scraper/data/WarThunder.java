package com.mochi.scraper.data;

import com.mochi.scraper.utils.DescriptionBuilder;
import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalTime;

public class WarThunder {
  public final Update pinnedNewsFeed;
  public final Update unpinnedNewsFeed;
  public final Update pinnedChangelogFeed;
  public final Update unpinnedChangelogFeed;
  private final JsoupConnector jsoupConnector;

  public WarThunder(JsoupConnector jsoupConnector) {
    this.pinnedNewsFeed = new Update();
    this.unpinnedNewsFeed = new Update();
    this.pinnedChangelogFeed = new Update();
    this.unpinnedChangelogFeed = new Update();
    this.jsoupConnector = jsoupConnector;
  }

  public static void main(String[] args) {
    WarThunder warThunder = new WarThunder(new JsoupConnector());

    try {
      warThunder.getPinnedNews();
      warThunder.getUnpinnedNews();
      warThunder.getPinnedChangelog();
      warThunder.getUnpinnedChangelog();

      PostBuilder.createNewsPost(warThunder.pinnedNewsFeed);
      PostBuilder.createNewsPost(warThunder.unpinnedNewsFeed);
      PostBuilder.createNewsPost(warThunder.pinnedChangelogFeed);
      PostBuilder.createNewsPost(warThunder.unpinnedChangelogFeed);
    }
    catch (IOException error) {
      System.err.println("Error: " + error.getMessage());
    }
  }

  public void getNewsFeed(Element entry, Update newsFeed) throws IOException {
    if (entry != null) {
      var title = entry.select("div.widget__title").text();
      newsFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.select("a.widget__link").attr("href");
      newsFeed.setUrl(postLink.isEmpty() ? "No url found" : "https://warthunder.com" + postLink);

      var widgetDiv = entry.selectFirst("div.widget__poster");
      if (widgetDiv == null) {
        newsFeed.setImage("No image found");
        return;
      }

      var imgPath = widgetDiv.selectFirst("img.widget__poster-media");
      if (imgPath == null) {
        newsFeed.setImage("No image found");
        return;
      }

      var imgUrl = imgPath.attr("data-src");
      if (imgUrl.isEmpty()) {
        imgUrl = imgPath.attr("src"); // If data-src is empty, try to fall back on src
      }

      if (imgUrl.startsWith("//")) {
        imgUrl = "https:" + imgUrl;
      }
      if (imgUrl.contains(" ")) {
        imgUrl = imgUrl.replace(" ", "%20");
      }

      newsFeed.setImage(imgUrl.isEmpty() ? "No image found" : imgUrl);

      var description = entry.select("div.widget__comment").text();
      description = Jsoup.parse(description).text();
      description = DescriptionBuilder.truncateDescription(description, 300);
      newsFeed.setDescription(
              description.isEmpty() ? "No description available" : description);
    }
    else {
      System.out.printf("[%s] [INFO] No entries found on warthunder.com\n", LocalTime.now());
    }
  }

  public void getPinnedNews() throws IOException {
    String url = "https://warthunder.com/en/news";
    var doc = jsoupConnector.connect(url, "warthunder.com");
    Element pinnedEntry = getEntry(doc, true);

    if (pinnedEntry != null) {
      getNewsFeed(pinnedEntry, pinnedNewsFeed);
    }
    else {
      System.out.printf("[%s] [INFO] No pinned entries found on warthunder.com\n", LocalTime.now());
    }
  }

  public void getUnpinnedNews() throws IOException {
    String url = "https://warthunder.com/en/news";
    var doc = jsoupConnector.connect(url, "warthunder.com");
    Element unpinnedEntry = getEntry(doc, false);

    if (unpinnedEntry != null) {
      getNewsFeed(unpinnedEntry, unpinnedNewsFeed);
    } else {
      System.out.printf("[%s] [INFO] No unpinned entries found on warthunder.com\n", LocalTime.now());
    }
  }

  public void getPinnedChangelog() throws IOException {
    String url = "https://warthunder.com/en/game/changelog/";
    var doc = jsoupConnector.connect(url, "warthunder.com");
    Element pinnedEntry = getEntry(doc, true);

    if (pinnedEntry != null) {
      getNewsFeed(pinnedEntry, pinnedChangelogFeed);
    }
    else {
      System.out.printf("[%s] [INFO] No pinned entries found on warthunder.com\n", LocalTime.now());
    }
  }

  public void getUnpinnedChangelog() throws IOException {
    String url = "https://warthunder.com/en/game/changelog/";
    var doc = jsoupConnector.connect(url, "warthunder.com");
    Element unpinnedEntry = getEntry(doc, false);

    if (unpinnedEntry != null) {
      getNewsFeed(unpinnedEntry, unpinnedChangelogFeed);
    } else {
      System.out.printf("[%s] [INFO] No unpinned entries found on warthunder.com\n", LocalTime.now());
    }
  }

  private Element getEntry(Document doc, boolean isPinned) {
    Elements entries = doc.select("div.showcase__item.widget");

    for (Element entry : entries) {
      boolean isPostPinned = !entry.select("div.widget__pin").isEmpty();
      if (isPostPinned == isPinned) {
        return entry;
      }
    }
    return null;
  }
}