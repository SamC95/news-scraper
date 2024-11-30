package com.example.scraper.utils;

import com.example.scraper.model.Update;

import java.io.IOException;

public class PostBuilder {
  public static void createNewsPost(Update post) throws IOException {
    if (post.getAuthor() != null) {
      System.out.println("Author: " + post.getAuthor());
    }

    if (post.getTitle() != null) {
      System.out.println("Title: " + post.getTitle());
    }

    if (post.getUrl() != null) {
      System.out.println("URL: " + post.getUrl());
    }

    if (post.getImage() != null) {
      System.out.println("Image: " + post.getImage());
    }

    if (post.getDescription() != null) {
      System.out.println("Description: " + post.getDescription());
    }

    System.out.print("\n");
  }
}
