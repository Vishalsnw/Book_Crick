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
        "🎉 Audience favorite! +18 earnings",
        "📱 Viral dance challenge! +30 earnings",
        "🌧️ Shooting cancelled due to rain! -12 earnings",
        "🍿 Surprise box office hit! +40 earnings",
        "🤝 Major brand tie-up! +22 earnings",
        "🛑 Script Leak Controversy! -15% earnings",
        "💰 Tax Raid! -25 earnings",
        "🔥 Social Media Boycott! -30 earnings",
        "✨ Viral BTS Clip! +12 earnings",
        "🎥 Cameo by a Superstar! +35 earnings"
    };

    public enum IndustryTrend {
        NORMAL("Normal Market", 1.0f),
        SOUTH_WAVE("The South Wave (Action/Thriller +30%)", 1.0f),
        COMEDY_BOOM("Comedy Boom (Comedy +25%)", 1.0f),
        STREAMING_TAKEOVER("Streaming Takeover (Lower theater, Safer returns)", 0.8f),
        ROMANCE_REVIVAL("Romance Revival (Romance +20%)", 1.0f);

        public String description;
        public float theaterMultiplier;
        IndustryTrend(String desc, float multi) { this.description = desc; this.theaterMultiplier = multi; }
    }

    public enum StarPower {
        NONE("No Star", 0, 1.0f),
        NEWCOMER("Newcomer", 5, 1.2f),
        RISING_STAR("Rising Star", 15, 1.5f),
        SUPERSTAR("Superstar", 40, 2.5f),
        MEGASTAR("Megastar", 70, 4.0f);

        public String name;
        public int budgetIncrease;
        public float earningsMultiplier;
        StarPower(String name, int budget, float multi) {
            this.name = name; this.budgetIncrease = budget; this.earningsMultiplier = multi;
        }
    }

    public static class RoundResults {
        public int baseEarnings;
        public int genreMultiplier;
        public int seasonalBonus;
        public int randomEventImpact;
        public int loanInterest;
        public int totalEarnings;
        public String genre;
        public String eventDescription;
        public float starRating;
        public StarPower cast;
        public IndustryTrend currentTrend;
    }

    public static RoundResults calculateRoundEarnings(MainActivity.Player player, int round, int year, IndustryTrend trend) {
        RoundResults result = new RoundResults();
        result.currentTrend = trend;
        
        // Randomly assign star power
        int starRoll = random.nextInt(100);
        if (starRoll < 5) result.cast = StarPower.MEGASTAR;
        else if (starRoll < 15) result.cast = StarPower.SUPERSTAR;
        else if (starRoll < 35) result.cast = StarPower.RISING_STAR;
        else if (starRoll < 60) result.cast = StarPower.NEWCOMER;
        else result.cast = StarPower.NONE;

        result.baseEarnings = random.nextInt(151);
        
        String[] genres = {"Action", "Drama", "Romance", "Horror", "Comedy", "Thriller", "Sci-Fi"};
        result.genre = genres[random.nextInt(genres.length)];
        
        switch (result.genre) {
            case "Action": 
                result.genreMultiplier = 160; 
                if (trend == IndustryTrend.SOUTH_WAVE) result.genreMultiplier += 30;
                break;
            case "Horror": result.genreMultiplier = 140; break;
            case "Drama": result.genreMultiplier = 90; break;
            case "Romance": 
                result.genreMultiplier = 130; 
                if (trend == IndustryTrend.ROMANCE_REVIVAL) result.genreMultiplier += 20;
                break;
            case "Comedy": 
                result.genreMultiplier = 120; 
                if (trend == IndustryTrend.COMEDY_BOOM) result.genreMultiplier += 25;
                break;
            case "Thriller": 
                result.genreMultiplier = 150; 
                if (trend == IndustryTrend.SOUTH_WAVE) result.genreMultiplier += 30;
                break;
            case "Sci-Fi": result.genreMultiplier = 170; break;
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
        
        // Calculate Total Earnings with Star Power and Trends
        float total = ((result.baseEarnings * result.genreMultiplier) / 100.0f) + 
                               result.seasonalBonus + result.randomEventImpact - result.loanInterest;
        
        total *= result.cast.earningsMultiplier;
        total *= trend.theaterMultiplier;
        
        // Add excitement: Sudden Box Office Crash or Surge
        if (random.nextInt(15) == 0) {
            float swing = 0.5f + (random.nextFloat() * 1.0f); // 0.5x to 1.5x
            total *= swing;
            if (swing > 1.3f) result.eventDescription = "🚀 BOX OFFICE SURGE! " + result.eventDescription;
            else if (swing < 0.7f) result.eventDescription = "📉 BOX OFFICE CRASH! " + result.eventDescription;
        }

        result.totalEarnings = Math.max(0, (int)total);
        
        // Generate Star Rating (1.0 to 5.0)
        result.starRating = 1.0f + (random.nextFloat() * 4.0f);
        if (result.totalEarnings > 200) result.starRating = Math.max(4.0f, result.starRating);
        else if (result.totalEarnings < 20) result.starRating = Math.min(2.5f, result.starRating);

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
