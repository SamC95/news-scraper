package com.example.scraper.data;

import org.jsoup.nodes.Element;

public class DescriptionBuilder {
  StringBuilder descBuilder = new StringBuilder();
  String shortString = "";

  public DescriptionBuilder(Element description, Patch patch) {
    buildDescription(description);

    if (description != null) {
      if (descBuilder.length() > 300) {
        shortString = descBuilder.substring(0, 299) + "...";
        patch.setDescription(shortString);
      } else {
        patch.setDescription(descBuilder.toString());
      }
    }
  }

  private void buildDescription(Element description) {
    if (description != null) {
      if (!description.ownText().isEmpty()) {
        descBuilder.append(description.ownText()).append("\n");
      }

      for (Element child : description.children()) {
        buildDescription(child);
      }
    }
  }

  private String findBetween(String s, String first, String last) {
    int start = s.indexOf(first) + first.length();
    int end = s.indexOf(last, start);
    return s.substring(start, end);
  }

  public static String truncateDescription(String description, int maxLength) {
    if (description.length() > maxLength) {
      int lastSpace = description.lastIndexOf(" ", maxLength);

      return (lastSpace == -1
              ? description.substring(0, maxLength)
              : description.substring(0, lastSpace))
          + "...";
    }
    return description;
  }
}