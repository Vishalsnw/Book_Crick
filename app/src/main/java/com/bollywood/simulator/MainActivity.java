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

public class MainActivity extends AppCompatActivity {
    private final String[] PLAYER_NAMES = {
        "Golu", "Amit Bagle", "Mangesh", "Vasim", "Amit Randhe", "Khushi", "Ajinkya", "Vinay",
        "Aashish", "Ashok Singh", "Sandip Basra", "Gokul", "Ritesh", "Bipin", "Ajit Bonde", "Amol Patil",
        "Hemant", "Ravi Patil", "Sachin Pardesi", "Sachin Patil", "Vishal", "Nitin", "Dipak Trivedi",
        "Sunil", "Charu", "Bhavesh Chaudhari", "Dipak R", "Mayur", "Nilesh", "Dipak BH", "Akshit"
    };

    private List<Player> players = new ArrayList<>();
    private List<String> oscarWinners = new ArrayList<>();
    private List<Player> playerHistory = new ArrayList<>();
    private Map<String, PlayerStats> playerStats = new HashMap<>();
    private List<Movie> movieArchive = new ArrayList<>();
    private int currentYear = 1;
    private int currentRound = 1;
    private String gameState = "START";
    private GameEngine.IndustryTrend currentTrend = GameEngine.IndustryTrend.NORMAL;
    private final Random random = new Random();
    private final Gson gson = new Gson();

    private TextView titleText, statsText, yearBadge, topMoviesText, eventText, nomineeText;
    private Button actionButton, oscarButton, incomeButton, profileButton, achieveButton;
    private LinearLayout topMoviesSection, oscarAnimationOverlay;
    private Handler animationHandler = new Handler(Looper.getMainLooper());

    private static final String PREFS_NAME = "BollywoodPrefs";
    private static final String KEY_OSCARS = "OscarWinners";
    private static final String KEY_YEAR = "CurrentYear";
    private static final String KEY_PLAYERS = "PlayerHistory";
    private static final String KEY_STATS = "PlayerStats";
    private static final String KEY_MOVIES = "MovieArchive";
    private String lastEvent = "";

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
        incomeButton = findViewById(R.id.incomeButton);
        profileButton = findViewById(R.id.profileButton);
        achieveButton = findViewById(R.id.achieveButton);
        topMoviesSection = findViewById(R.id.topMoviesSection);
        eventText = findViewById(R.id.eventText);
        oscarAnimationOverlay = findViewById(R.id.oscarAnimationOverlay);
        nomineeText = findViewById(R.id.nomineeText);

        loadData();

        actionButton.setOnClickListener(v -> handleButtonClick());
        oscarButton.setOnClickListener(v -> startActivity(new Intent(this, OscarListActivity.class)));
        incomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerIncomeActivity.class);
            intent.putExtra("players", new ArrayList<>(players));
            startActivity(intent);
        });
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerProfileActivity.class);
            if (!players.isEmpty()) {
                intent.putExtra("playerName", players.get(0).name);
            }
            startActivity(intent);
        });
        achieveButton.setOnClickListener(v -> startActivity(new Intent(this, AchievementsActivity.class)));
        
        updateUI();
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
        
        // Set a random trend for the year
        GameEngine.IndustryTrend[] trends = GameEngine.IndustryTrend.values();
        currentTrend = trends[random.nextInt(trends.length)];
        
        for (String name : PLAYER_NAMES) {
            PlayerStats stats = playerStats.getOrDefault(name, new PlayerStats(name));
            Player existingPlayer = findPlayerInHistory(name);
            
            int budget = random.nextInt(91) + 10;
            if (existingPlayer != null && existingPlayer.balance >= budget) {
                // Use existing balance, no new loan
                players.add(new Player(name, 0, existingPlayer.balance));
            } else {
                // Take a loan
                players.add(new Player(name, budget, existingPlayer != null ? existingPlayer.balance : 0));
            }
            
            stats.yearsActive++;
            playerStats.put(name, stats);
        }
        
        gameState = "ROUND1";
        topMoviesSection.setVisibility(View.GONE);
        lastEvent = "🎬 New year begins! Players ready their films...";
        updateUI();
    }

    private Player findPlayerInHistory(String name) {
        for (Player p : playerHistory) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        return null;
    }

    private void playRound(int advanceCount, String nextState) {
        List<Player> activePlayers = new ArrayList<>();
        List<MovieRecord> roundMovies = new ArrayList<>();
        List<String> roundEvents = new ArrayList<>();

        for (Player p : players) {
            if (p != null && p.active) {
                GameEngine.RoundResults results = GameEngine.calculateRoundEarnings(p, currentRound, currentYear, currentTrend);
                
                if (results != null) {
                    p.lastEarnings = results.totalEarnings;
                    p.earnings += results.totalEarnings;
                    p.balance = p.earnings - p.loan;
                    
                    activePlayers.add(p);
                    roundMovies.add(new MovieRecord(p.name, results.totalEarnings, results.starRating, results.cast));
                    
                    String genreStr = (results.genre != null) ? results.genre : "Unknown";
                    Movie movie = new Movie(p.name, genreStr, results.totalEarnings, currentRound, currentYear, results.totalEarnings > 50);
                    movie.starRating = results.starRating;
                    movieArchive.add(movie);
                    
                    PlayerStats stats = playerStats.getOrDefault(p.name, new PlayerStats(p.name));
                    if (stats != null) {
                        stats.addMovie(movie);
                        
                        if (GameEngine.checkBankruptcy(p)) {
                            p.active = false;
                            stats.bankruptcies++;
                            roundEvents.add("💥 " + p.name + " filed for bankruptcy!");
                        } else {
                            String achievement = GameEngine.getAchievementForPerformance(p, activePlayers.size() - 1, activePlayers.size());
                            if (achievement != null) {
                                stats.addAchievement(achievement);
                            }
                        }
                        playerStats.put(p.name, stats);
                    }
                }
            }
        }

        Collections.sort(activePlayers, (a, b) -> Integer.compare(b.lastEarnings, a.lastEarnings));
        for (int i = 0; i < Math.min(advanceCount, activePlayers.size()); i++) {
            activePlayers.get(i).active = true;
        }
        for (int i = advanceCount; i < activePlayers.size(); i++) {
            activePlayers.get(i).active = false;
        }

        if (nextState != null && nextState.equals("ROUND 2")) {
            showTopMovies(roundMovies, 5, "Top 5 Movies");
        }

        if (nextState != null && nextState.equals("ROUND 3")) {
            showTopMovies(roundMovies, 3, "Top 3 Movies");
        }

        if (!roundEvents.isEmpty()) {
            lastEvent = roundEvents.get(0);
        }

        if (nextState.equals("WINNER")) {
            startOscarAnimation(activePlayers, nextState);
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

        List<Player> finalFour = new ArrayList<>();
        for (int i = 0; i < Math.min(4, activePlayers.size()); i++) {
            finalFour.add(activePlayers.get(i));
        }
        Collections.shuffle(finalFour);

        showNomineeWithDelay(finalFour, 0, activePlayers.get(0), nextState);
    }

    private void showNomineeWithDelay(List<Player> nominees, int index, Player winner, String nextState) {
        if (index < nominees.size()) {
            nomineeText.setText(nominees.get(index).name);
            nomineeText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_scale_in));
            
            animationHandler.postDelayed(() -> showNomineeWithDelay(nominees, index + 1, winner, nextState), 2000);
        } else {
            nomineeText.setText("🏆 WINNER 🏆\n" + winner.name);
            nomineeText.setTextColor(android.graphics.Color.parseColor("#FFD700"));
            nomineeText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_scale_in));

            animationHandler.postDelayed(() -> {
                oscarAnimationOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
                    oscarAnimationOverlay.setVisibility(View.GONE);
                    nomineeText.setTextColor(android.graphics.Color.WHITE);
                    finalizeYear(winner, nextState);
                });
            }, 4000);
        }
    }

    private void finalizeYear(Player winner, String nextState) {
        oscarWinners.add("Year " + currentYear + ": " + winner.name + " (₹" + winner.earnings + ")");
        
        PlayerStats winnerStats = playerStats.getOrDefault(winner.name, new PlayerStats(winner.name));
        winnerStats.oscarWins++;
        winnerStats.addAchievement("🏆 Oscar Winner");
        playerStats.put(winner.name, winnerStats);
        
        for (Player p : players) {
            playerHistory.add(new Player(p.name, p.loan, p.balance));
        }
        
        // Ensure player stats are persistent across years by updating them in history
        for (Player p : players) {
            PlayerStats stats = playerStats.getOrDefault(p.name, new PlayerStats(p.name));
            stats.addMovie(new Movie(p.name, "Year End", p.earnings, 0, currentYear, false));
            playerStats.put(p.name, stats);
        }
        
        currentYear++;
        currentRound = 0;
        saveData();
        
        gameState = nextState;
        updateUI();
    }

    private void showTopMovies(List<MovieRecord> movies, int count, String title) {
        Collections.sort(movies, (a, b) -> Integer.compare(b.earnings, a.earnings));
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
              .append("\n  Earnings: ₹").append(m.earnings).append("\n\n");
        }
        TextView sectionTitle = (TextView) topMoviesSection.getChildAt(0);
        sectionTitle.setText("🎬 " + title);
        topMoviesText.setText(sb.toString());
        topMoviesSection.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        yearBadge.setText("Year " + currentYear);
        
        if (!lastEvent.isEmpty()) {
            eventText.setText("NEWS: " + lastEvent + "\n(Trend: " + currentTrend.description + ")");
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
        Collections.sort(sorted, (a, b) -> Integer.compare(b.balance, a.balance));

        sb.append(String.format("%-3s | %-15s | %-10s\n", "R", "Name", "Balance"));
        sb.append("---------------------------------\n");
        for (int i = 0; i < sorted.size(); i++) {
            Player p = sorted.get(i);
            String rankSymbol = (i == 0) ? "🥇" : (i == 1) ? "🥈" : (i == 2) ? "🥉" : String.format("%02d", i + 1);
            sb.append(String.format("%-3s | %-15s | ₹%-10d\n", rankSymbol, p.name, p.balance));
        }

        statsText.setText(sb.toString());
    }

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_OSCARS, gson.toJson(oscarWinners));
        editor.putInt(KEY_YEAR, currentYear);
        editor.putString(KEY_PLAYERS, gson.toJson(playerHistory));
        editor.putString(KEY_STATS, gson.toJson(playerStats));
        editor.putString(KEY_MOVIES, gson.toJson(movieArchive));
        editor.apply();
    }

    private void loadData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String oscarsJson = prefs.getString(KEY_OSCARS, null);
        if (oscarsJson != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            oscarWinners = gson.fromJson(oscarsJson, type);
        }
        
        String playerJson = prefs.getString(KEY_PLAYERS, null);
        if (playerJson != null) {
            Type playerType = new TypeToken<ArrayList<Player>>() {}.getType();
            playerHistory = gson.fromJson(playerJson, playerType);
        }
        
        String statsJson = prefs.getString(KEY_STATS, null);
        if (statsJson != null) {
            Type statsType = new TypeToken<HashMap<String, PlayerStats>>() {}.getType();
            playerStats = gson.fromJson(statsJson, statsType);
        }
        
        String moviesJson = prefs.getString(KEY_MOVIES, null);
        if (moviesJson != null) {
            Type moviesType = new TypeToken<ArrayList<Movie>>() {}.getType();
            movieArchive = gson.fromJson(moviesJson, moviesType);
        }
        
        currentYear = prefs.getInt(KEY_YEAR, 1);
    }

    public static class Player implements Serializable {
        public String name;
        public int loan, earnings, balance, lastEarnings;
        public boolean active = true;
        
        public Player(String name, int loan, int carryoverBalance) {
            this.name = name;
            this.loan = loan;
            this.earnings = carryoverBalance;
            this.balance = carryoverBalance - loan;
        }
    }

    private static class MovieRecord {
        String playerName;
        int earnings;
        float rating;
        GameEngine.StarPower cast;
        MovieRecord(String name, int earn, float rate, GameEngine.StarPower star) { 
            this.playerName = name; this.earnings = earn; this.rating = rate; this.cast = star;
        }
    }
}
