package com.example.scraper.data;

import com.example.scraper.model.Update;
import com.example.scraper.utils.PostBuilder;
import com.example.scraper.utils.SteamRSSParser;

import java.io.IOException;

/*
Utilising Monster Hunter Wilds Steam news page to retrieve posts
 */
public class MonsterHunterWilds {
    private final Update newsFeed;

    public MonsterHunterWilds() {
        this.newsFeed = new Update();
    }

    public static void main(String[] args) {
        MonsterHunterWilds monsterHunterWilds = new MonsterHunterWilds();

        try {
            SteamRSSParser.getSteamRSSNewsFeed("2246340", monsterHunterWilds.newsFeed);

            PostBuilder.createNewsPost(monsterHunterWilds.newsFeed);
        }
        catch (IOException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }
}
