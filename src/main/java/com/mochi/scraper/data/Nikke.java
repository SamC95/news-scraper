package com.mochi.scraper.data;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.mochi.scraper.model.Update;
import com.mochi.scraper.utils.PlaywrightConnector;
import com.mochi.scraper.utils.PostBuilder;
import com.mochi.scraper.utils.TextFormatter;

import java.time.LocalTime;

public class Nikke {
  public final Update newsFeed;
  private final PlaywrightConnector playwrightConnector;

  public Nikke(PlaywrightConnector playwrightConnector) {
    this.newsFeed = new Update();
    this.playwrightConnector = playwrightConnector;
  }

  public static void main(String[] args) {
    Nikke nikke = new Nikke(new PlaywrightConnector());

    try {
      nikke.getNewsFeed();

      PostBuilder.createNewsPost(nikke.newsFeed);
    } catch (Exception e) {
      System.err.printf("[%s] [ERROR] %s\n", LocalTime.now(), e.getMessage());
    }
  }

  // TODO - Add proper testing that this is always retrieving the most recent post
  public void getNewsFeed() {
    String url = "https://nikke-en.com/index.html";

    var page = playwrightConnector.connect(url);

    ElementHandle entry = page.querySelector(".swiper-slide.swiper-slide-active");

    if (entry != null) {
      String title = entry.querySelector(".news_swiper_name").textContent();
      title = TextFormatter.removeWhiteSpace(title);
      this.newsFeed.setTitle(title);

      ElementHandle imgElement = entry.querySelector(".news_swiper_img");
      if (imgElement != null) {
        // Wait for the image element to be visible and loaded
        page.waitForSelector(
            ".news_swiper_img[src]", new Page.WaitForSelectorOptions().setTimeout(5000));

        String imgUrl = imgElement.getAttribute("src");
        if (imgUrl != null && !imgUrl.isEmpty()) {
          this.newsFeed.setImage(imgUrl);
        } else {
          System.err.println("Image URL not found or not loaded");
          this.newsFeed.setImage("No image found");
        }
      } else {
        System.err.println("Image element not found");
        this.newsFeed.setImage("No image found");
      }

      ElementHandle linkElement = entry.querySelector("a");

      if (linkElement != null) {
        String newsUrl = linkElement.getAttribute("href");
        newsUrl = newsUrl.replace("./", "/");
        newsUrl = "https://nikke-en.com" + newsUrl;
        this.newsFeed.setUrl(newsUrl);
      }
      else {
        System.err.printf("[%s] [ERROR] Link for NIKKE news post not found\n", LocalTime.now());
      }
    }
    else {
      System.err.printf("[%s] [ERROR] No entry found for NIKKE\n", LocalTime.now());
    }

    playwrightConnector.close();
  }
}
