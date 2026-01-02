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
        public float baseEarnings;
        public int genreMultiplier;
        public int seasonalBonus;
        public int randomEventImpact;
        public int loanInterest;
        public float totalEarnings;
        public String genre;
        public String eventDescription;
        public float starRating;
        public StarPower cast;
        public IndustryTrend currentTrend;
        public boolean isHit;
    }

    private static final String[] DECADE_EVENTS = {
        "📱 Streaming Revolution! +50% budget needed but safer returns",
        "🍿 Cinema Tax Break! +20% earnings for all",
        "📉 Global Theater Closure! -30% earnings",
        "🎭 New Wave Cinema! Drama/Indie genres +40%",
        "🤖 AI Scripting! Production costs -15%"
    };

    private static final String[] RANDOM_EVENTS = {
        "🎭 Scandal! Actor caught in party brawl - ₹15 penalty",
        "🎵 Viral Track! Soundtrack trending on reels + ₹20 bonus",
        "📰 Bad Reviews! Critics call it 'unwatchable' - ₹10 penalty",
        "🌟 Masterpiece! National award buzz + ₹25 bonus",
        "🎬 Star Power! A-list superstar signs for a cameo + ₹30 bonus",
        "😢 Production Delay! Monsoons wash away sets - ₹12 penalty",
        "📱 PR Stunt! Fake dating rumors drive hype + ₹18 bonus",
        "💰 Tax Raid! Hidden cash found in office - ₹35 penalty",
        "🔥 Boycott! Social media outrage over a dialogue - ₹25 penalty",
        "🎥 Technical Glitch! CGI fails in climax - ₹15 penalty",
        "✨ Midnight Show! Fans go crazy in single screens + ₹22 bonus",
        "🍿 Sold Out! Multiplexes adding extra shows + ₹28 bonus",
        "🛑 Script Leak! Climax revealed on Reddit - ₹18 penalty",
        "🤝 Global Tie-up! Hollywood studio buys remake rights + ₹40 bonus",
        "🎙️ Podcast Rant! Lead actor says something controversial - ₹12 penalty"
    };

    public static RoundResults calculateRoundEarnings(MainActivity.Player player, int round, int year, IndustryTrend trend) {
        RoundResults result = new RoundResults();
        float total = random.nextFloat() * 100.0f; // Base 0-100
        
        // Random Events (25% chance)
        if (random.nextInt(4) == 0) {
            int eventIdx = random.nextInt(RANDOM_EVENTS.length);
            result.eventDescription = RANDOM_EVENTS[eventIdx];
            if (result.eventDescription.contains("+ ₹")) {
                try {
                    total += Integer.parseInt(result.eventDescription.split("₹")[1].split(" ")[0]);
                } catch (Exception e) {}
            } else if (result.eventDescription.contains("- ₹")) {
                try {
                    total -= Integer.parseInt(result.eventDescription.split("₹")[1].split(" ")[0]);
                } catch (Exception e) {}
            }
        } else {
            result.eventDescription = "Smooth release";
        }

        result.totalEarnings = Math.min(100.0f, Math.max(0.0f, total));
        result.starRating = 1.0f + (random.nextFloat() * 4.0f);
        result.isHit = result.totalEarnings > 50;
        
        String[] genres = {"Action", "Drama", "Romance", "Horror", "Comedy", "Thriller", "Sci-Fi"};
        result.genre = genres[random.nextInt(genres.length)];
        result.cast = StarPower.NONE;
        
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
