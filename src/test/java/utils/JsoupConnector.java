package utils;

import org.jsoup.nodes.Document;

import java.io.IOException;

// Unused currently, just for reference
public interface JsoupConnector {
    Document connect(String url) throws IOException;
}
