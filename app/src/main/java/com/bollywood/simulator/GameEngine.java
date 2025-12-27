package com.bollywood.simulator;

import java.util.Random;

public class GameEngine {
    private static final Random random = new Random();

    private static final String[] RANDOM_EVENTS = {
        "🎭 Actor scandal! -20 earnings",
        "🎵 Hit soundtrack! +15 earnings",
        "📰 Bad reviews! -10 earnings",
        "🌟 Critical acclaim! +20 earnings",
        "🎬 Famous director signed! +25 earnings",
        "⭐ Award nomination! +10 earnings",
        "😢 Production delays! -15 earnings",
        "🎉 Audience favorite! +18 earnings"
    };

    public static class RoundResults {
        public int baseEarnings;
        public int genreMultiplier;
        public int seasonalBonus;
        public int randomEventImpact;
        public int loanInterest;
        public int totalEarnings;
        public String genre;
        public String eventDescription;
    }

    public static RoundResults calculateRoundEarnings(MainActivity.Player player, int round, int year) {
        RoundResults result = new RoundResults();
        
        result.baseEarnings = random.nextInt(101);
        
        String[] genres = {"Action", "Drama", "Romance", "Horror", "Comedy"};
        result.genre = genres[random.nextInt(genres.length)];
        
        switch (result.genre) {
            case "Action": result.genreMultiplier = 150; break;
            case "Horror": result.genreMultiplier = 150; break;
            case "Drama": result.genreMultiplier = 80; break;
            case "Romance": result.genreMultiplier = 120; break;
            case "Comedy": result.genreMultiplier = 110; break;
            default: result.genreMultiplier = 100;
        }
        
        int seasonalBonus = 0;
        if (round == 1 || round == 2) {
            seasonalBonus = 20;
        } else if (round == 3) {
            seasonalBonus = -10;
        }
        result.seasonalBonus = seasonalBonus;
        
        int eventImpact = 0;
        String eventDesc = "Normal market conditions";
        if (random.nextInt(3) == 0) {
            int eventIdx = random.nextInt(RANDOM_EVENTS.length);
            eventDesc = RANDOM_EVENTS[eventIdx];
            eventImpact = random.nextInt(31) - 15;
        }
        result.randomEventImpact = eventImpact;
        result.eventDescription = eventDesc;
        
        int loanInterest = 0;
        if (player.loan > 0) {
            loanInterest = (int)(player.loan * 0.12);
        }
        result.loanInterest = loanInterest;
        
        result.totalEarnings = (int)((result.baseEarnings * result.genreMultiplier) / 100.0) + 
                               result.seasonalBonus + result.randomEventImpact - result.loanInterest;
        result.totalEarnings = Math.max(0, result.totalEarnings);
        
        return result;
    }

    public static boolean checkBankruptcy(MainActivity.Player player) {
        return player.balance < -500;
    }

    public static String getAchievementForPerformance(MainActivity.Player player, int position, int totalPlayers) {
        if (position == 1) return "🏆 Round Winner";
        if (position <= 3) return "🥈 Top 3 Finisher";
        if (position <= 5) return "⭐ Rising Star";
        if (player.balance > 1000) return "💰 Rich and Famous";
        if (player.balance < -300) return "🔴 On the Edge";
        return null;
    }
}
