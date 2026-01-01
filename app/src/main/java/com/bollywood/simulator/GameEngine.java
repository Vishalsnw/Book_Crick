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
        NONE("No Star", 0, 1.0f, 0),
        NEWCOMER("Newcomer", 5, 1.2f, 1),
        RISING_STAR("Rising Star", 15, 1.5f, 2),
        SUPERSTAR("Superstar", 40, 2.5f, 3),
        MEGASTAR("Megastar", 70, 4.0f, 4);

        public String name;
        public int budgetIncrease;
        public float earningsMultiplier;
        public int level;
        StarPower(String name, int budget, float multi, int lvl) {
            this.name = name; this.budgetIncrease = budget; this.earningsMultiplier = multi; this.level = lvl;
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
        public boolean isHit;
    }

    public static RoundResults calculateRoundEarnings(MainActivity.Player player, int round, int year, IndustryTrend trend) {
        RoundResults result = new RoundResults();
        result.currentTrend = trend;
        
        // Dynamic Popularity: Start with player's current star power
        result.cast = player.currentStar;
        
        // Chance to naturally upgrade/downgrade based on last performance
        if (player.lastEarnings > 80 && result.cast.level < 4 && random.nextInt(100) < 20) {
            result.cast = StarPower.values()[result.cast.level + 1];
        } else if (player.lastEarnings < 20 && result.cast.level > 0 && random.nextInt(100) < 15) {
            result.cast = StarPower.values()[result.cast.level - 1];
        }
        player.currentStar = result.cast;

        result.baseEarnings = random.nextInt(101); // 0 to 100
        
        String[] genres = {"Action", "Drama", "Romance", "Horror", "Comedy", "Thriller", "Sci-Fi"};
        result.genre = genres[random.nextInt(genres.length)];
        
        // ROI-based success logic (Budget matters)
        int effectiveBudget = 40 + result.cast.budgetIncrease;
        
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
        
        int loanInterest = (int)(player.loan * 0.12);
        result.loanInterest = loanInterest;
        
        float total = result.baseEarnings;
        total = (total * result.genreMultiplier) / 100.0f;
        total += result.seasonalBonus;
        total += result.randomEventImpact;
        total *= result.cast.earningsMultiplier;
        total *= trend.theaterMultiplier;
        
        total -= result.loanInterest;
        total -= effectiveBudget;
        
        if (random.nextInt(15) == 0) {
            float swing = 0.5f + (random.nextFloat() * 1.0f);
            total *= swing;
            if (swing > 1.3f) result.eventDescription = "🚀 BOX OFFICE SURGE! " + result.eventDescription;
            else if (swing < 0.7f) result.eventDescription = "📉 BOX OFFICE CRASH! " + result.eventDescription;
        }

        result.totalEarnings = Math.min(100, Math.max(0, (int)total));
        
        // ROI Hit Detection: Earnings > Budget * 1.5 is a hit
        result.isHit = result.totalEarnings > (effectiveBudget * 1.2);
        
        result.starRating = 1.0f + (random.nextFloat() * 4.0f);
        if (result.isHit) result.starRating = Math.max(3.5f, result.starRating);

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
