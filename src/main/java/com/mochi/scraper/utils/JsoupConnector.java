package com.mochi.scraper.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupConnector {
  public Document connect(String url, String domain) throws IOException {
    String randomUserAgent = UserAgent.getUserAgent();

    try {
      return Jsoup.connect(url)
          .userAgent(randomUserAgent)
          .header("Accept-Language", "en-US,en;q=0.9")
          .header(
              "Accept",
              "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
          .header("Referer", "https://www.google.com")
          .get();
    }
    catch (IOException error) {
      throw new IOException("Couldn't connect to " + domain + ": " + error.getMessage());
    }
  }
}
