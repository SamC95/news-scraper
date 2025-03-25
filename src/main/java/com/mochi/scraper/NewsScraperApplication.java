package com.mochi.scraper;

import com.mochi.scraper.data.FinalFantasyXI;
import com.mochi.scraper.data.FinalFantasyXIV;
import com.mochi.scraper.data.HellLetLoose;
import com.mochi.scraper.data.MonsterHunterWilds;
import com.mochi.scraper.data.Overwatch;
import com.mochi.scraper.data.SatisfactoryGame;
import com.mochi.scraper.data.TheOldRepublic;
import com.mochi.scraper.data.WarThunder;
import com.mochi.scraper.data.WorldOfWarcraft;
import com.mochi.scraper.utils.JsoupConnector;
import com.mochi.scraper.utils.SteamRSSParser;

import java.io.IOException;

public class NewsScraperApplication {
    public static void main(String[] args) throws IOException {
        FinalFantasyXI finalFantasyXI = new FinalFantasyXI(new JsoupConnector());

        finalFantasyXI.getTopicFeed();
        finalFantasyXI.getInformationFeed();

        FinalFantasyXIV finalFantasyXIV = new FinalFantasyXIV(new JsoupConnector());

        finalFantasyXIV.getTopicFeed();
        finalFantasyXIV.getNewsFeed();

        HellLetLoose hellLetLoose = new HellLetLoose(new JsoupConnector());

        SteamRSSParser.getSteamRSSNewsFeed(
                "686810", hellLetLoose.newsFeed, hellLetLoose.jsoupConnector);

        MonsterHunterWilds monsterHunterWilds = new MonsterHunterWilds(new JsoupConnector());

        SteamRSSParser.getSteamRSSNewsFeed(
                "2246340", monsterHunterWilds.newsFeed, monsterHunterWilds.jsoupConnector);

        Overwatch overwatch = new Overwatch(new JsoupConnector());

        overwatch.getPatchNotes();

        SatisfactoryGame satisfactoryGame = new SatisfactoryGame(new JsoupConnector());

        SteamRSSParser.getSteamRSSNewsFeed(
                "526870", satisfactoryGame.newsFeed, satisfactoryGame.jsoupConnector);

        TheOldRepublic theOldRepublic = new TheOldRepublic(new JsoupConnector());

        theOldRepublic.getNewsFeed();

        WarThunder warThunder = new WarThunder(new JsoupConnector());

        warThunder.getPinnedNews();
        warThunder.getUnpinnedNews();

        WorldOfWarcraft worldOfWarcraft = new WorldOfWarcraft(new JsoupConnector());

        worldOfWarcraft.getNewsFeed();
    }
}
