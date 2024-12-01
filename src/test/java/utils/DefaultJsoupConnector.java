package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

// Unused currently, just for reference
public class DefaultJsoupConnector implements JsoupConnector {
    @Override
    public Document connect(String url) throws IOException {
        return Jsoup.connect(url).userAgent("Mozilla/5.0").get();
    }
}
