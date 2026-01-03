package com.bollywood.simulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_OSCARS_SAVE = "oscar_winners";
    private static final String KEY_YEAR_SAVE = "current_year";
    private static final String KEY_PLAYERS_SAVE = "players";
    private static final String KEY_STATS_SAVE = "player_stats";
    private static final String KEY_MOVIES_SAVE = "movie_archive";
    
    private String lastEventMsg = "";

    private final String[] PLAYER_NAMES = {
        "Golu", "Amit Bagle", "Mangesh", "Vasim", "Amit Randhe", "Khushi", "Ajinkya", "Vinay",
        "Aashish", "Ashok Singh", "Sandip Basra", "Gokul", "Ritesh", "Bipin", "Ajit Bonde", "Amol Patil",
        "Hemant", "Ravi Patil", "Sachin Pardesi", "Sachin Patil", "Vishal", "Nitin", "Dipak Trivedi",
        "Sunil", "Charu", "Bhavesh Chaudhari", "Dipak R", "Mayur", "Nilesh", "Dipak BH", "Akshit", "Suraj"
    };

    private List<Player> players = new ArrayList<>();
    private List<String> oscarWinners = new ArrayList<>();
    private List<Player> playerHistory = new ArrayList<>();
    private Map<String, PlayerStats> playerStats = new HashMap<>();
    private List<Movie> movieArchive = new ArrayList<>();
    private StockMarket stockMarket = new StockMarket();
    private int currentYear = 1;
    private int currentRound = 1;
    private String gameState = "START";
    private GameEngine.IndustryTrend currentTrend = GameEngine.IndustryTrend.NORMAL;
    private final Random random = new Random();
    private final Gson gson = new Gson();

    private TextView titleText, statsText, yearBadge, topMoviesText, eventText, nomineeText;
    private Button actionButton, oscarButton, incomeButton, profileButton, achieveButton, stockButton;
    private LinearLayout topMoviesSection, oscarAnimationOverlay;
    private Handler animationHandler = new Handler(Looper.getMainLooper());
    private static final String PREFS_NAME = "BollywoodPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.titleText);
        statsText = findViewById(R.id.statsText);
        yearBadge = findViewById(R.id.yearBadge);
        topMoviesText = findViewById(R.id.topMoviesText);
        actionButton = findViewById(R.id.actionButton);
        oscarButton = findViewById(R.id.oscarButton);
        profileButton = findViewById(R.id.profileButton);
        achieveButton = findViewById(R.id.achieveButton);
        stockButton = findViewById(R.id.stockButton);
        topMoviesSection = findViewById(R.id.topMoviesSection);
        eventText = findViewById(R.id.eventText);
        oscarAnimationOverlay = findViewById(R.id.oscarAnimationOverlay);
        nomineeText = findViewById(R.id.nomineeText);

        // Load data first to populate playerHistory and playerStats
        loadData();

        actionButton.setOnClickListener(v -> handleButtonClick());
        oscarButton.setOnClickListener(v -> startActivity(new Intent(this, OscarListActivity.class)));
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerProfileActivity.class);
            if (!players.isEmpty()) {
                intent.putExtra("playerName", players.get(0).name);
            }
            startActivity(intent);
        });
        achieveButton.setOnClickListener(v -> startActivity(new Intent(this, AchievementsActivity.class)));
        stockButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LiveTradingActivity.class);
            intent.putExtra("stockMarket", stockMarket);
            startActivity(intent);
        });

        // Only setup initial players if loading didn't restore any
        if (players.isEmpty() && playerHistory.isEmpty()) {
            setupInitialPlayers();
        } else if (players.isEmpty() && !playerHistory.isEmpty()) {
            // Restore current year state if available
            for (Player p : playerHistory) {
                players.add(new Player(p.name, p.loan, (int)p.balance));
            }
        }
        updateUI();
    }

    private void setupInitialPlayers() {
        players.clear();
        for (String name : PLAYER_NAMES) {
            int budget = 10 + random.nextInt(91); // 10 to 100
            players.add(new Player(name, budget, 0));
        }
    }

    private void handleButtonClick() {
        switch (gameState) {
            case "START": startNewYear(); break;
            case "ROUND1": playRound(16, "ROUND 2"); break;
            case "ROUND 2": playRound(8, "ROUND 3"); break;
            case "ROUND 3": playRound(4, "SEMI-FINAL"); break;
            case "SEMI-FINAL": playRound(2, "FINAL"); break;
            case "FINAL": playRound(1, "WINNER"); break;
            case "WINNER": gameState = "START"; updateUI(); break;
        }
    }

    private void startNewYear() {
        players.clear();
        currentRound = 1;
        
        lastEventMsg = "📢 SYSTEM: Rank is now based on NET WORTH (Cash + Studio Shares)!\n🤖 BOTS: 300+ AI Traders are now active on the Exchange.";
        
        for (String name : PLAYER_NAMES) {
            float carryoverBalance = 0;
            Player histP = null;
            for (Player hp : playerHistory) {
                // Remove title from history name for comparison if needed
                String cleanName = hp.name;
                if (cleanName.contains(" ")) {
                    String[] parts = cleanName.split(" ");
                    cleanName = parts[parts.length - 1];
                }

                if (cleanName.equals(name)) {
                    carryoverBalance = hp.balance;
                    histP = hp;
                    break;
                }
            }

            int budget = 10 + random.nextInt(91); // Random budget between 10-100
            Player newPlayer;
            
            if (carryoverBalance >= budget) {
                newPlayer = new Player(name, 0, (int)carryoverBalance);
                newPlayer.balance = carryoverBalance - budget;
            } else {
                newPlayer = new Player(name, budget, (int)carryoverBalance);
                newPlayer.balance = carryoverBalance - budget;
            }
            
            if (histP != null) {
                newPlayer.nominationCount = histP.nominationCount;
                newPlayer.oscarWins = histP.oscarWins;
            }
            players.add(newPlayer);
        }
        
        lastPositions.clear();
        gameState = "ROUND1";
        topMoviesSection.setVisibility(View.GONE);
        lastEventMsg = "🎬 Year " + currentYear + " begins!";
        updateUI();
    }

    private void playRound(int advanceCount, String nextState) {
        List<Player> activePlayers = new ArrayList<>();
        List<MovieRecord> roundMovies = new ArrayList<>();
        List<String> roundEvents = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            // Every active player produces a movie in their respective round
            if (p != null && p.active) {
                GameEngine.RoundResults results = GameEngine.calculateRoundEarnings(p, currentRound, currentYear, currentTrend);
                
                if (results != null) {
                    p.lastEarnings = results.totalEarnings;
                    p.earnings += results.totalEarnings; // This is cumulative for the year
                    p.balance += results.totalEarnings; // This is cumulative for their career
                    
                    activePlayers.add(p);
                    roundMovies.add(new MovieRecord(p.name, results.totalEarnings, results.starRating, results.cast));
                    
                    String genreStr = (results.genre != null) ? results.genre : "Unknown";
                    Movie movie = new Movie(p.name, genreStr, results.totalEarnings, currentRound, currentYear, results.isHit);
                    movie.starRating = results.starRating;
                    movieArchive.add(movie);
                    
                    PlayerStats stats = playerStats.getOrDefault(p.name, new PlayerStats(p.name));
                    if (stats != null) {
                        stats.addMovie(movie);
                        String achievement = GameEngine.getAchievementForPerformance(p, activePlayers.size() - 1, activePlayers.size());
                        if (achievement != null) {
                            stats.addAchievement(achievement);
                        }
                        playerStats.put(p.name, stats);
                    }
                }
            }
        }

        Collections.sort(activePlayers, (a, b) -> Float.compare(b.lastEarnings, a.lastEarnings));
        
        // Advance players based on round logic
        for (Player p : players) {
            p.active = false;
        }
        for (int i = 0; i < Math.min(advanceCount, activePlayers.size()); i++) {
            activePlayers.get(i).active = true;
        }

        if (nextState != null && nextState.equals("ROUND 2")) {
            showTopMovies(roundMovies, 5, "Top 5 Movies");
        }

        if (nextState != null && nextState.equals("ROUND 3")) {
            showTopMovies(roundMovies, 3, "Top 3 Movies");
        }

        if (!roundEvents.isEmpty()) {
            lastEventMsg = roundEvents.get(0);
        }

        // Nomination logic: The 4 players entering Round 4 (Semi-Final) are the nominees
        // Round 4 is the Semi-Final, Round 5 is the Final.
        // People reaching Round 4 get a nomination (N)
        // The person who wins Round 5 (the winner of the final duel) gets the Oscar (🏆)
        if (nextState.equals("FINAL")) {
            // Nomination logic: The 4 players entering Round 4 (Semi-Final) are the nominees
            startOscarAnimation(activePlayers, nextState);
        } else if (nextState.equals("WINNER")) {
             // Round 5 (Final) has concluded. The winner of this round is the Oscar Winner.
             finalizeYear(activePlayers.get(0), nextState);
        } else {
            currentRound++;
            gameState = nextState;
            updateUI();
        }
    }

    private void startOscarAnimation(List<Player> activePlayers, String nextState) {
        oscarAnimationOverlay.setVisibility(View.VISIBLE);
        oscarAnimationOverlay.setAlpha(0f);
        oscarAnimationOverlay.animate().alpha(1f).setDuration(500);

        List<Player> nominees = new ArrayList<>();
        // The 4 players who qualified for the Semi-Final (Round 4) are the nominees
        for (int i = 0; i < Math.min(4, activePlayers.size()); i++) {
            Player p = activePlayers.get(i);
            nominees.add(p);
            p.nominationCount++;
        }
        
        Collections.shuffle(nominees);

        // Reset text and button
        nomineeText.setText("Click to reveal first nominee...");
        nomineeText.setTextColor(android.graphics.Color.WHITE); // Reset color
        actionButton.setText("Next Nominee");
        
        final int[] currentIndex = {0};
        final Player finalWinner = activePlayers.get(0);
        
        actionButton.setOnClickListener(v -> {
            if (currentIndex[0] < nominees.size()) {
                nomineeText.setText(nominees.get(currentIndex[0]).name);
                nomineeText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_scale_in));
                currentIndex[0]++;
                if (currentIndex[0] == nominees.size()) {
                    actionButton.setText("Reveal Winner");
                }
            } else if (currentIndex[0] == nominees.size()) {
                nomineeText.setText("🏆 WINNER 🏆\n" + finalWinner.name);
                nomineeText.setTextColor(android.graphics.Color.parseColor("#FFD700"));
                nomineeText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_scale_in));
                actionButton.setText("Finish Year");
                currentIndex[0]++;
            } else {
                oscarAnimationOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
                    oscarAnimationOverlay.setVisibility(android.view.View.GONE);
                    nomineeText.setTextColor(android.graphics.Color.WHITE);
                    actionButton.setOnClickListener(v2 -> handleButtonClick());
                    finalizeYear(finalWinner, nextState);
                });
            }
        });
    }

    private void finalizeYear(Player winner, String nextState) {
        oscarWinners.add("Year " + currentYear + ": " + winner.name + " (₹" + String.format("%.2f", winner.earnings) + ")");
        
        PlayerStats winnerStats = playerStats.getOrDefault(winner.name, new PlayerStats(winner.name));
        winnerStats.oscarWins++;
        winnerStats.addAchievement("🏆 Oscar Winner");
        playerStats.put(winner.name, winnerStats);

        // Generate Year-End News
        List<String> yearEndNews = new ArrayList<>();
        List<Player> sortedByBalance = new ArrayList<>(players);
        Collections.sort(sortedByBalance, (a, b) -> Float.compare(b.balance, a.balance));
        
        String[] wealthNews = {
            "💰 %s ends the year as the undisputed King of Box Office!",
            "💎 Luxury cars and big mansions: %s is the wealthiest producer in town!",
            "📈 Stock market alert: Shares of %s's production house are soaring!",
            "🤑 Cash flow! %s is drowning in profits this year.",
            "🏦 Mega Merger? Rumors suggest %s might acquire a rival studio!",
            "🌆 The skyline of Mumbai belongs to %s, the new real estate tycoon."
        };
        yearEndNews.add(String.format(wealthNews[random.nextInt(wealthNews.length)], sortedByBalance.get(0).name));
        
        Player mostHits = players.get(0);
        int maxHits = 0;
        for (Player p : players) {
            PlayerStats ps = playerStats.get(p.name);
            int p_hits = 0;
            if (ps != null) {
                for (Movie m : ps.movieHistory) {
                    if (m.wasHit && m.year == currentYear) p_hits++;
                }
            }
            if (p_hits > maxHits) {
                maxHits = p_hits;
                mostHits = p;
            }
        }
        if (maxHits > 0) {
            String[] hitNews = {
                "🎬 Midas Touch! Every movie %s touched this year became a hit!",
                "🔥 %s is on fire with %d back-to-back blockbusters!",
                "🌟 The industry is bowing down to the new Hit Machine: %s.",
                "🎥 Record breaking year! %s delivers %d massive hits.",
                "🎞️ Scriptwriters are lining up outside %s's office for a chance to work!",
                "🍿 Popcorn sales are at an all-time high thanks to %s's hits!"
            };
            String news = hitNews[random.nextInt(hitNews.length)];
            if (news.contains("%d")) {
                yearEndNews.add(String.format(news, mostHits.name, maxHits));
            } else {
                yearEndNews.add(String.format(news, mostHits.name));
            }
        }

        List<Player> lowPerformers = new ArrayList<>();
        for (Player p : players) {
            if (p.balance < -200) lowPerformers.add(p);
        }
        if (!lowPerformers.isEmpty()) {
            Collections.shuffle(lowPerformers);
            String[] brokeNews = {
                "📉 Financial Crisis: %s is struggling to pay back massive loans.",
                "💸 Empty pockets! %s's production house is facing a liquidity crunch.",
                "🏚️ From Riches to Rags: Can %s survive another year of flops?",
                "⚠️ Bankruptcy warning for %s as debt continues to pile up.",
                "🕯️ Selling the family silver? %s seen leaving a pawn shop.",
                "🚫 Production halted! %s can't even afford tea for the crew."
            };
            yearEndNews.add(String.format(brokeNews[random.nextInt(brokeNews.length)], lowPerformers.get(0).name));
        }
        
        String[] industryNews = {
            "🎭 Bollywood Trend: Audiences are rejecting star-kids and demanding better scripts!",
            "🎥 Cinema Strike! Production was halted for a month, but we are back.",
            "🎟️ Ticket prices hiked! Producers are happy, audiences are not.",
            "📱 OTT vs Theaters: The war for content heats up!",
            "🕺 Item numbers are back in fashion, much to the critics' dismay.",
            "📽️ A major award show was cancelled due to a backstage brawl!",
            "📈 Sensex up! Bollywood stocks are becoming a favorite for investors.",
            "📸 Paparazzi alert: A major star was spotted at a secret director's meeting."
        };
        yearEndNews.add(industryNews[random.nextInt(industryNews.length)]);
        
        yearEndNews.add("🏆 " + winner.name + " wins the prestigious Oscar for Year " + currentYear + "!");

        lastEventMsg = yearEndNews.get(new Random().nextInt(yearEndNews.size()));

        // Update oscarWins in the current player object for UI persistence
        for (Player p : players) {
            if (p.name.equals(winner.name)) {
                p.oscarWins++;
                break;
            }
        }
        
        playerHistory.clear();
        for (Player p : players) {
            Player historyPlayer = new Player(p.name, p.loan, (int)p.balance);
            historyPlayer.age = p.age;
            historyPlayer.currentStar = p.currentStar;
            historyPlayer.oscarWins = p.oscarWins;
            historyPlayer.nominationCount = p.nominationCount;
            
            // Add Titles/Ranks
            String title = "";
            if (p.oscarWins >= 5) title = "🏆 LEGEND";
            else if (p.oscarWins >= 3) title = "🌟 SUPERSTAR";
            else if (p.balance > 5000) title = "💰 TYCOON";
            else if (p.balance > 2000) title = "🎬 MOGUL";
            else if (p.balance < -500) title = "📉 STRUGGLER";
            historyPlayer.name = (title.isEmpty() ? "" : title + " ") + p.name;
            
            playerHistory.add(historyPlayer);
        }
        
        // Ensure player stats are persistent across years by updating them in history
        for (Player p : players) {
            PlayerStats stats = playerStats.getOrDefault(p.name, new PlayerStats(p.name));
            stats.addMovie(new Movie(p.name, "Year End", p.earnings, 0, currentYear, false));
            playerStats.put(p.name, stats);
        }
        
        currentYear++;
        currentRound = 1;
        stockMarket.updateMarket(players);
        saveData();
        
        gameState = "START";
        updateUI();
    }

    private void showTopMovies(List<MovieRecord> movies, int count, String title) {
        Collections.sort(movies, (a, b) -> Float.compare(b.earnings, a.earnings));
        StringBuilder sb = new StringBuilder();
        sb.append("🔥 TREND: ").append(currentTrend.description.toUpperCase()).append("\n\n");
        for (int i = 0; i < Math.min(count, movies.size()); i++) {
            MovieRecord m = movies.get(i);
            String stars = "";
            for(int s=0; s<(int)m.rating; s++) stars += "⭐";
            if (m.rating % 1 >= 0.5) stars += "✨";
            
            String hitStatus = m.earnings > 150 ? " [BLOCKBUSTER 🚀]" : m.earnings > 80 ? " [HIT 🎬]" : m.earnings < 30 ? " [FLOP 📉]" : "";
            
            sb.append("#").append(i + 1).append(" ").append(m.playerName).append(hitStatus)
              .append("\n  ").append(m.cast.name).append(" | ").append(stars).append(" (").append(String.format("%.1f", m.rating)).append(")")
              .append("\n  Earnings: ₹").append(String.format("%.2f", m.earnings)).append("\n\n");
        }
        TextView sectionTitle = (TextView) topMoviesSection.getChildAt(0);
        sectionTitle.setText("🎬 " + title);
        topMoviesText.setText(sb.toString());
        topMoviesSection.setVisibility(View.VISIBLE);
    }

    private Map<String, Integer> lastPositions = new HashMap<>();

    private void updateUI() {
        yearBadge.setText("Year " + currentYear);
        
        if (!lastEventMsg.isEmpty()) {
            eventText.setText("NEWS: " + lastEventMsg + "\n(Trend: " + currentTrend.description + ")");
            eventText.setVisibility(View.VISIBLE);
        } else {
            eventText.setVisibility(View.GONE);
        }
        
        if (gameState.equals("WINNER")) {
            Player winner = null;
            for (Player p : players) if (p.active) winner = p;
            statsText.setText("🏆 OSCAR WINNER: " + (winner != null ? winner.name : "N/A") + "\nFinal Balance: ₹" + (winner != null ? winner.balance : 0));
            actionButton.setText("Start Next Year");
            return;
        }

        actionButton.setText(gameState.equals("START") ? "Start Game" : "Next Round (" + gameState + ")");

        StringBuilder sb = new StringBuilder();
        List<Player> sorted = new ArrayList<>(players);
        Collections.sort(sorted, (a, b) -> {
            StockMarket.SharePrice spa = null;
            StockMarket.SharePrice spb = null;
            for(StockMarket.SharePrice s : stockMarket.stocks) {
                if(s.producerName.equals(a.name)) spa = s;
                if(s.producerName.equals(b.name)) spb = s;
            }
            float valA = a.balance + (spa != null ? spa.currentPrice * 10 : 0);
            float valB = b.balance + (spb != null ? spb.currentPrice * 10 : 0);
            return Float.compare(valB, valA);
        });

            // Updated for Net Worth ranking: Rank | Studio | NetVal | 🏆 | N
            sb.append(String.format("%-2s | %-12s | %-6s | %s | %s\n", "R", "Studio/Name", "NetVal", "🏆", "N"));
            sb.append("--------------------------------------------------\n");
            for (int i = 0; i < sorted.size(); i++) {
                Player p = sorted.get(i);
                String rankSymbol = (i == 0) ? "🥇" : (i == 1) ? "🥈" : (i == 2) ? "🥉" : String.format("%02d", i + 1);
                
                // Net worth calculation: Cash + (Shares * Price)
                StockMarket.SharePrice sp = null;
                for(StockMarket.SharePrice s : stockMarket.stocks) {
                    if(s.producerName.equals(p.name)) { sp = s; break; }
                }
                float stockValue = (sp != null ? sp.currentPrice * 10 : 0);
                float netWorth = p.balance + stockValue;

                // Position trend arrow
                String trendArrow = "";
                if (lastPositions.containsKey(p.name)) {
                    int lastPos = lastPositions.get(p.name);
                    if (i < lastPos) trendArrow = " ⬆️";
                    else if (i > lastPos) trendArrow = " ⬇️";
                }
                
                // Add office milestone icon
                String office = "";
                if (netWorth > 5000) office = "🏰";
                else if (netWorth > 2000) office = "🏢";
                else if (netWorth > 500) office = "🏘️";

                String name = p.name;
                if (name.length() > 8) name = name.substring(0, 7) + "..";
                
                PlayerStats stats = playerStats.get(p.name);
                int oscars = (stats != null) ? stats.oscarWins : p.oscarWins;
                int noms = p.nominationCount;
                
                String stockPriceStr = sp != null ? String.format("₹%.0f", sp.currentPrice) : "";
                
                sb.append(String.format("%-2s | %s%-10s | %-6.0f | %-2d | %-2d %s%s\n",
                        rankSymbol, office, name, netWorth, oscars, noms, trendArrow, stockPriceStr));
            }

        // Update positions for next time
        lastPositions.clear();
        for (int i = 0; i < sorted.size(); i++) {
            lastPositions.put(sorted.get(i).name, i);
        }

        statsText.setText(sb.toString());
    }

    private void saveData() {
        try {
            File file = new File(getFilesDir(), "save_data.json");
            FileOutputStream fos = new FileOutputStream(file);
            Map<String, Object> data = new HashMap<>();
            data.put(KEY_OSCARS_SAVE, oscarWinners);
            data.put(KEY_YEAR_SAVE, currentYear);
            data.put(KEY_PLAYERS_SAVE, playerHistory);
            data.put(KEY_STATS_SAVE, playerStats);
            data.put(KEY_MOVIES_SAVE, movieArchive);
            fos.write(gson.toJson(data).getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            File file = new File(getFilesDir(), "save_data.json");
            if (!file.exists()) return;
            
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            fis.close();

            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = gson.fromJson(sb.toString(), type);
            
            if (data.containsKey(KEY_OSCARS_SAVE)) {
                oscarWinners = gson.fromJson(gson.toJson(data.get(KEY_OSCARS_SAVE)), new TypeToken<ArrayList<String>>(){}.getType());
            }
            if (data.containsKey(KEY_YEAR_SAVE)) {
                Object yearObj = data.get(KEY_YEAR_SAVE);
                if (yearObj instanceof Double) {
                    currentYear = ((Double) yearObj).intValue();
                } else if (yearObj instanceof Integer) {
                    currentYear = (Integer) yearObj;
                }
            }
            if (data.containsKey(KEY_PLAYERS_SAVE)) {
                playerHistory = gson.fromJson(gson.toJson(data.get(KEY_PLAYERS_SAVE)), new TypeToken<ArrayList<Player>>(){}.getType());
            }
            if (data.containsKey(KEY_STATS_SAVE)) {
                playerStats = gson.fromJson(gson.toJson(data.get(KEY_STATS_SAVE)), new TypeToken<HashMap<String, PlayerStats>>(){}.getType());
            }
            if (data.containsKey(KEY_MOVIES_SAVE)) {
                movieArchive = gson.fromJson(gson.toJson(data.get(KEY_MOVIES_SAVE)), new TypeToken<ArrayList<Movie>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Player implements Serializable {
        public String name;
        public int loan, oscarWins, nominationCount;
        public float balance;
        public float earnings, lastEarnings;
        public boolean active = true;
        public GameEngine.StarPower currentStar = GameEngine.StarPower.NONE;
        public int age = 20 + new java.util.Random().nextInt(15);
        
        public Player(String name, int loan, int carryoverBalance) {
            this.name = name;
            this.loan = loan;
            this.earnings = 0; // Reset annual earnings
            this.balance = (float)carryoverBalance; // Initial balance before budget
            this.oscarWins = 0;
            this.nominationCount = 0;
        }
    }

    private static class MovieRecord {
        String playerName;
        float earnings;
        float rating;
        GameEngine.StarPower cast;
        MovieRecord(String name, float earn, float rate, GameEngine.StarPower star) { 
            this.playerName = name; this.earnings = earn; this.rating = rate; this.cast = star;
        }
    }
}
