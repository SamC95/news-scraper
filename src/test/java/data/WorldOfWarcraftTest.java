package data;

import com.mochi.scraper.data.WorldOfWarcraft;
import com.mochi.scraper.utils.JsoupConnector;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
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
public class WorldOfWarcraftTest {

  private WorldOfWarcraft worldOfWarcraft;

  @Mock private JsoupConnector jsoupConnector;

  String mockHtml =
          """
                  <div class="List-item">
                      <div class="NewsBlog-title">New Expansion Announced</div>
                      <a class="Link NewsBlog-link" href="/news/123456">Read more</a>
                      <img class="NewsBlog-image" data-src="//cdn.blizzard.com/news-image.jpg" />
                      <p class="NewsBlog-desc">Blizzard announces a new expansion for World of Warcraft...</p>
                  </div>
                  """;

  @BeforeEach
  void setUp() {
    worldOfWarcraft = new WorldOfWarcraft(jsoupConnector);
  }

  @Test
  public void getNewsFeedShouldCorrectlyProvideResponse()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    Document mockDoc = Parser.parse(mockHtml, "https://worldofwarcraft.blizzard.com");

    when(jsoupConnector.connect(
            "https://worldofwarcraft.blizzard.com/en-gb/news", "worldofwarcraft.blizzard.com"))
        .thenReturn(mockDoc);

    worldOfWarcraft.getNewsFeed();

    Field newsFeedField = WorldOfWarcraft.class.getDeclaredField("newsFeed");
    newsFeedField.setAccessible(true);
    var newsFeed = newsFeedField.get(worldOfWarcraft);

    assertEquals("New Expansion Announced", FieldValueHelper.getFieldValue(newsFeed, "title"));

    assertEquals(
        "https://worldofwarcraft.blizzard.com/en-gb/news/123456",
        FieldValueHelper.getFieldValue(newsFeed, "url"));

    assertEquals(
        "https://cdn.blizzard.com/news-image.jpg",
        FieldValueHelper.getFieldValue(newsFeed, "image"));

    assertEquals(
        "Blizzard announces a new expansion for World of Warcraft...",
        FieldValueHelper.getFieldValue(newsFeed, "description"));
  }
}
