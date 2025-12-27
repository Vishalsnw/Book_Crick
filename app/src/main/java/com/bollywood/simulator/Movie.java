package com.bollywood.simulator;

import java.io.Serializable;

public class Movie implements Serializable {
    public String playerName;
    public String genre;
    public int earnings;
    public int round;
    public int year;
    public boolean wasHit;
    public String title;

    public Movie(String playerName, String genre, int earnings, int round, int year, boolean wasHit) {
        this.playerName = playerName;
        this.genre = genre;
        this.earnings = earnings;
        this.round = round;
        this.year = year;
        this.wasHit = wasHit;
        
        String[] genres = {"Action", "Drama", "Romance", "Horror", "Comedy"};
        String[] titles = {"Mumbai Dreams", "Monsoon Love", "Hidden Secrets", "Midnight Chase", "City Lights",
                           "Silver Screen", "Golden Hour", "Eternal Bond", "Revenge Games", "New Beginning"};
        
        this.title = titles[((playerName.hashCode() + round + year) % titles.length)] + 
                     " - " + genres[((playerName.hashCode() + earnings) % genres.length)];
    }
}
