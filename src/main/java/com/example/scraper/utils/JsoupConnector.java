package com.example.scraper.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupConnector {
    public Document connect(String url, String domain) throws IOException {
        try {
            return Jsoup.connect(url).userAgent("Mozilla").get();
        }
        catch (IOException error) {
            throw new IOException("Couldn't connect to " + domain + ": " + error.getMessage());
        }
    }
}
