package com.bollywood.simulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerStats implements Serializable {
    public String playerName;
    public int totalMovies = 0;
    public float totalEarnings = 0;
    public int oscarWins = 0;
    public int bankruptcies = 0;
    public int longestWinStreak = 0;
    public int currentWinStreak = 0;
    public List<String> achievements = new ArrayList<>();
    public List<Movie> movieHistory = new ArrayList<>();
    public boolean retired = false;
    public int yearsActive = 0;

    public PlayerStats(String playerName) {
        this.playerName = playerName;
    }

    public void addMovie(Movie movie) {
        movieHistory.add(movie);
        totalMovies++;
        totalEarnings += Math.max(0, movie.earnings);
        if (movie.wasHit) {
            currentWinStreak++;
            if (currentWinStreak > longestWinStreak) {
                longestWinStreak = currentWinStreak;
            }
        } else {
            currentWinStreak = 0;
        }
    }

    public void addAchievement(String achievement) {
        if (!achievements.contains(achievement)) {
            achievements.add(achievement);
        }
    }
}
