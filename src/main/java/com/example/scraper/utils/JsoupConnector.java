package com.example.scraper.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;

public class JsoupConnector {
  public Document connect(String url, String domain) throws IOException {
    String randomUserAgent = getUserAgent();

    try {
      return Jsoup.connect(url)
              .userAgent(randomUserAgent)
              .header("Accept-Language", "en-US,en;q=0.9")
              .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
              .header("Referer", "https://www.google.com")
              .get();
    }
    catch (IOException error) {
      throw new IOException("Couldn't connect to " + domain + ": " + error.getMessage());
    }
  }

  private String getUserAgent() {
    String[] userAgents = {
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36",
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.1 Safari/537.36",
      "Mozilla/5.0 (X11; Ubuntu; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.1 Safari/537.36",
      "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
    };

    Random random = new Random();
    return userAgents[random.nextInt(userAgents.length)];
  }
}
