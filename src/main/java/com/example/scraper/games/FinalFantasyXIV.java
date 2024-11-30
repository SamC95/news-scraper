package com.example.scraper.games;

import com.example.scraper.data.Patch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;

public class FinalFantasyXIV {
  private final String name = "Final Fantasy XIV";
  private final Patch topicFeed;
  private final Patch newsFeed;
  private final String fileName = "thumbnails/final-fantasy-xiv-meteor-logo.png";

  public FinalFantasyXIV() {
    this.topicFeed = new Patch();
    this.newsFeed = new Patch();
  }

  private InputStream getFileFromResourceAsStream(String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();

    return classLoader.getResourceAsStream(fileName);
  }

  public static void main(String[] args) {
    FinalFantasyXIV finalFantasyXIV = new FinalFantasyXIV();
    InputStream thumbnailStream = null;

    thumbnailStream = finalFantasyXIV.getFileFromResourceAsStream(finalFantasyXIV.fileName);

    if (thumbnailStream == null) {
      System.err.println("Couldn't find file: " + finalFantasyXIV.fileName);
    }
    else {
      System.out.println("Thumbnail file: " + finalFantasyXIV.fileName + " loaded successfully.");
    }

    try {
      finalFantasyXIV.getTopicInfo();
      finalFantasyXIV.getNewsInfo();

      finalFantasyXIV.displayPostInfo(finalFantasyXIV.topicFeed);
      finalFantasyXIV.displayPostInfo(finalFantasyXIV.newsFeed);

    } catch (Exception error) {
      System.out.println("Error: " + error.getMessage());
    }
  }

  // Gets data from Lodestone Topics RSS feed
  public void getTopicInfo() throws IOException {
    String url = "https://eu.finalfantasyxiv.com/lodestone/news/topics.xml";

    var doc = connect(url);

    Element entry = doc.select("entry").first();

    if (entry != null) {
      var title = entry.select("title").text();
      this.topicFeed.setTitle(title.isEmpty() ? "No title available" : title);

      var postLink = entry.select("link[rel=alternate]").attr("href");
      this.topicFeed.setUrl(postLink.isEmpty() ? "No url found" : postLink);

      var contentHtml = entry.select("content").html();
      var decodedHtml = StringEscapeUtils.unescapeHtml4(contentHtml);
      Document document = Jsoup.parse(decodedHtml);

      Element img = document.select("img").first();

      if (img != null) {
        var imgUrl = img.attr("src");
        this.topicFeed.setImage(imgUrl);
      }
      else {
        System.out.println("No image found");
      }

      var description = entry.select("summary").text();
      description = Jsoup.parse(description).text();
      description = truncateDescription(description, 300);
      this.topicFeed.setDescription(description.isEmpty() ? "No description available" : description);

      var author = entry.select("author name").text();
      if (author.isEmpty()) {
        author = "No author found.";
      }
      this.topicFeed.setAuthor(author);
    }
    else {
      System.out.println("No entries found in Lodestone RSS Feed");
    }
  }

  // Gets data from Lodestone News RSS feed
  public void getNewsInfo() throws IOException {
    String url = "https://eu.finalfantasyxiv.com/lodestone/news/news.xml";

    var doc = connect(url);

    Element entry = doc.select("entry").first();

    if (entry != null) {
      var title = entry.select("title").text();
      this.newsFeed.setTitle(title);

      var postLink = entry.select("link[rel=alternate]").attr("href");
      this.newsFeed.setUrl(postLink);

      var description = entry.select("content").text();
      description = Jsoup.parse(description).text();
      description = truncateDescription(description, 100);
      this.newsFeed.setDescription(description);

      var author = entry.select("author name").text();
      this.newsFeed.setAuthor(author);
    }
    else {
      System.out.println("No entries found in Lodestone RSS Feed");
    }
  }

  private void displayPostInfo(Patch post) throws IOException {
    System.out.println("Author: " + post.getAuthor());
    System.out.println("Title: " + post.getTitle());
    System.out.println("URL: " + post.getUrl());

    // Only print image if not null -- Topic RSS should have images, news RSS should not
    if (post.getImage() != null) {
      System.out.println("Image: " + post.getImage());
    }

    System.out.println("Description: " + post.getDescription() + "\n");
  }

  private Document connect(String url) throws IOException {
    try {
        return Jsoup.connect(url).userAgent("Mozilla/5.0").get();
    }
    catch (IOException error) {
      throw new IOException("Couldn't connect to Lodestone RSS Feed: " + error.getMessage());
    }
  }

  private String truncateDescription(String description, int maxLength) {
    if (description.length() > maxLength) {
      int lastSpace = description.lastIndexOf(" ", maxLength);

      return (lastSpace == -1 ? description.substring(0, maxLength) : description.substring(0, lastSpace)) + "...";
    }
    return description;
  }
}
