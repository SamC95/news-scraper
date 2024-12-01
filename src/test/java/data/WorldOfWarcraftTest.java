package data;

import com.example.scraper.data.WorldOfWarcraft;
import com.example.scraper.utils.JsoupConnector;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorldOfWarcraftTest {

  private WorldOfWarcraft worldOfWarcraft;

  @Mock private JsoupConnector jsoupConnector;

  @BeforeEach
  void setUp() {
    worldOfWarcraft = new WorldOfWarcraft(jsoupConnector);
  }

  @Test
  public void getNewsFeedShouldCorrectlyProvideResponse()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    String mockHtml =
        """
                <div class="List-item">
                    <div class="NewsBlog-title">New Expansion Announced</div>
                    <a class="Link NewsBlog-link" href="/news/123456">Read more</a>
                    <img class="NewsBlog-image" data-src="//cdn.blizzard.com/news-image.jpg" />
                    <p class="NewsBlog-desc">Blizzard announces a new expansion for World of Warcraft...</p>
                </div>
                """;

    Document mockDoc = Parser.parse(mockHtml, "https://worldofwarcraft.blizzard.com");

    when(jsoupConnector.connect(
            "https://worldofwarcraft.blizzard.com/en-gb/news", "worldofwarcraft.blizzard.com"))
        .thenReturn(mockDoc);

    worldOfWarcraft.getNewsFeed();

    Field newsFeedField = WorldOfWarcraft.class.getDeclaredField("newsFeed");
    newsFeedField.setAccessible(true);
    var newsFeed = newsFeedField.get(worldOfWarcraft);

    assertEquals("New Expansion Announced", getFieldValue(newsFeed, "title"));

    assertEquals(
        "https://worldofwarcraft.blizzard.com/en-gb/news/123456", getFieldValue(newsFeed, "url"));

    assertEquals("https://cdn.blizzard.com/news-image.jpg", getFieldValue(newsFeed, "image"));

    assertEquals(
        "Blizzard announces a new expansion for World of Warcraft...",
        getFieldValue(newsFeed, "description"));
  }

  private String getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    Field field = obj.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return (String) field.get(obj);
  }
}
