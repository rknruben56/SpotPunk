package edu.depaul.csc472.spotpunk;

import java.util.Random;

/**
 * Search Term Library used to get random songs
 * Created by rrodr on 11/11/2017.
 */

public class SearchTermRepository {

    // Random number generator
    private Random rand;

    // Search keywords used for randomness
    private static String[] searchKeywords = {
            "rock",
            "punk",
            "rap",
            "pop",
            "love",
            "hate",
            "disco",
            "lady",
            "rosie",
            "school",
            "chicago",
            "memory",
            "techno",
            "tree",
            "red",
            "blue",
            "green",
            "hot",
            "cold",
            "highway",
            "jump",
            "classical",
            "blood",
            "first",
            "royal",
            "queen",
            "panic",
            "moon",
            "alive",
            "dead"
    };

    SearchTermRepository() {
        rand = new Random();
    }

    /**
     * Returns a random search term
     * @return search term
     */
    public String getSearchTerm() {
        return searchKeywords[rand.nextInt(searchKeywords.length - 1)];
    }
}
