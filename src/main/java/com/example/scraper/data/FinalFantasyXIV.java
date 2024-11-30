package com.example.scraper.data;

import com.example.scraper.utils.DescriptionBuilder;
import com.example.scraper.model.Update;
import com.example.scraper.utils.PostBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;

public class FinalFantasyXIV {
  private final Update topicFeed;
  private final Update newsFeed;
  private final String fileName = "thumbnails/final-fantasy-xiv-meteor-logo.png";

  public FinalFantasyXIV() {
    this.topicFeed = new Update();
    this.newsFeed = new Update();
  }

  private InputStream getFileFromResourceAsStream(String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();

    return classLoader.getResourceAsStream(fileName);
  }

  public static void main(String[] args) {
    FinalFantasyXIV finalFantasyXIV = new FinalFantasyXIV();
    InputStream thumbnailStream;

    thumbnailStream = finalFantasyXIV.getFileFromResourceAsStream(finalFantasyXIV.fileName);

    if (thumbnailStream == null) {
      System.err.println("Couldn't find file: " + finalFantasyXIV.fileName);
    }
    else {
      System.out.println("Thumbnail file: " + finalFantasyXIV.fileName + " loaded successfully.");
    }

    try {
      finalFantasyXIV.getTopicFeed();
      finalFantasyXIV.getNewsFeed();

      PostBuilder.createNewsPost(finalFantasyXIV.topicFeed);
      PostBuilder.createNewsPost(finalFantasyXIV.newsFeed);

    }
    catch (Exception error) {
      System.out.println("Error: " + error.getMessage());
    }
  }

  // Gets data from Lodestone Topics RSS feed
  public void getTopicFeed() throws IOException {
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
      description = DescriptionBuilder.truncateDescription(description, 300);
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
  public void getNewsFeed() throws IOException {
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
      description = DescriptionBuilder.truncateDescription(description, 100);
      this.newsFeed.setDescription(description);

      var author = entry.select("author name").text();
      this.newsFeed.setAuthor(author);
    }
    else {
      System.out.println("No entries found in Lodestone RSS Feed");
    }
  }

  private Document connect(String url) throws IOException {
    try {
        return Jsoup.connect(url).userAgent("Mozilla/5.0").get();
    }
    catch (IOException error) {
      throw new IOException("Couldn't connect to Lodestone RSS Feed: " + error.getMessage());
    }
  }
}