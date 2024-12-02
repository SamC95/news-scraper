package data;

import com.example.scraper.data.WarThunder;
import com.example.scraper.utils.JsoupConnector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.FieldValueHelper;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WarThunderTest {
  private WarThunder warThunder;

  @Mock private JsoupConnector jsoupConnector;

  String mockHtml =
          """
          <!DOCTYPE html>
          <html lang="en">
          <head><meta charset="UTF-8"><title>Mock News</title></head>
          <body>
            <div class="showcase__item widget">
              <div class="widget__title">Sample News Title</div>
              <a class="widget__link" href="/en/news/12345-Sample-News">Read more</a>
              <div class="widget__poster">
                <img class="widget__poster-media" data-src="//cdn.warthunder.com/sample-image.jpg" alt="News Image">
              </div>
              <div class="widget__comment">
                <p>This is a sample news description that provides details about the news.</p>
              </div>
            </div>
          </body>
          </html>
          """;

  @BeforeEach
  void setUp() {
    warThunder = new WarThunder(jsoupConnector);
  }

  @Test
  public void getNewsFeedShouldCorrectlyProvideResponse()
      throws IOException, NoSuchFieldException, IllegalAccessException {

    Document mockDoc = Jsoup.parse(mockHtml);

    when(jsoupConnector.connect("https://warthunder.com/en/news", "warthunder.com"))
        .thenReturn(mockDoc);

    warThunder.getNewsFeed();

    Field newsFeedField = WarThunder.class.getDeclaredField("newsFeed");
    newsFeedField.setAccessible(true);
    var newsFeed = newsFeedField.get(warThunder);

    assertEquals("Sample News Title", FieldValueHelper.getFieldValue(newsFeed, "title"));

    assertEquals(
        "https://warthunder.com/en/news/12345-Sample-News",
        FieldValueHelper.getFieldValue(newsFeed, "url"));

    assertEquals(
        "https://cdn.warthunder.com/sample-image.jpg", FieldValueHelper.getFieldValue(newsFeed, "image"));

    assertEquals(
        "This is a sample news description that provides details about the news.",
        FieldValueHelper.getFieldValue(newsFeed, "description"));
  }
}
