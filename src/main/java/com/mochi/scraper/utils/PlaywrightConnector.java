package com.mochi.scraper.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;

import java.time.LocalTime;
import java.util.Map;

public class PlaywrightConnector {
  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;
  private Page page;

  public Page connect(String url) {
    String randomUserAgent = UserAgent.getUserAgent();

    try {
      playwright = Playwright.create();
      browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
      context =
          browser.newContext(
              new Browser.NewContextOptions()
                  .setUserAgent(randomUserAgent)
                  .setExtraHTTPHeaders(
                      Map.of(
                          "Accept-Language", "en-US,en;q=0.9",
                          "Referer", "https://www.google.com")));

      page = context.newPage();
      page.navigate(url);

      page.waitForLoadState(LoadState.NETWORKIDLE);

      return page;
    } catch (Exception e) {
      System.err.printf("[%s] [ERROR] Playwright failed to connect to %s\n", LocalTime.now(), url);
    }
    return null;
  }

  public void close() {
    if (page != null) {
      page.close();
    }
    if (context != null) {
      context.close();
    }
    if (browser != null) {
      browser.close();
    }
    if (playwright != null) {
      playwright.close();
    }
  }
}
