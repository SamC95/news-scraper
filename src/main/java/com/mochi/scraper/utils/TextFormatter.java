package com.mochi.scraper.utils;

public class TextFormatter {
    public static String removeWhiteSpace(String text) {
        text = text.replaceAll("\\s+", " ");
        text = text.replaceAll("^\\s+", "");

        return text;
    }
}
