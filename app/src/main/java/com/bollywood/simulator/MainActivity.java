package com.bollywood.simulator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final String[] PLAYER_NAMES = {
        "Golu", "Amit Bagle", "Mangesh", "Vasim", "Amit Randhe", "Khushi", "Ajinkya", "Vinay",
        "Aashish", "Ashok Singh", "Sandip Basra", "Gokul", "Ritesh", "Bipin", "Ajit Bonde", "Amol Patil",
        "Hemant", "Ravi Patil", "Sachin Pardesi", "Sachin Patil", "Vishal", "Nitin", "Dipak Trivedi",
        "Sunil", "Charu", "Bhavesh Chaudhari", "Dipak R", "Mayur", "Nilesh", "Dipak BH", "Sunil"
    };

    private List<Player> players = new ArrayList<>();
    private List<String> oscarWinners = new ArrayList<>();
    private List<MovieRecord> allTimeHighGrossing = new ArrayList<>();
    private int currentYear = 1;
    private String gameState = "START";
    private Random random = new Random();

    private TextView titleText, statsText, yearBadge, topMoviesText;
    private Button actionButton;
    private LinearLayout topMoviesSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.titleText);
        statsText = findViewById(R.id.statsText);
        yearBadge = findViewById(R.id.yearBadge);
        topMoviesText = findViewById(R.id.topMoviesText);
        actionButton = findViewById(R.id.actionButton);
        topMoviesSection = findViewById(R.id.topMoviesSection);

        actionButton.setOnClickListener(v -> handleButtonClick());
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
        for (String name : PLAYER_NAMES) {
            int budget = random.nextInt(91) + 10;
            players.add(new Player(name, budget));
        }
        gameState = "ROUND1";
        topMoviesSection.setVisibility(View.GONE);
        updateUI();
    }

    private void playRound(int advanceCount, String nextState) {
        List<Player> activePlayers = new ArrayList<>();
        List<MovieRecord> roundMovies = new ArrayList<>();

        for (Player p : players) {
            if (p.active) {
                int earnings = random.nextInt(101);
                p.earnings += earnings;
                p.balance = p.earnings - p.loan;
                p.lastEarnings = earnings;
                activePlayers.add(p);
                roundMovies.add(new MovieRecord(p.name, earnings));
                allTimeHighGrossing.add(new MovieRecord(p.name, earnings));
            }
        }

        Collections.sort(activePlayers, (a, b) -> Integer.compare(b.lastEarnings, a.lastEarnings));
        for (int i = 0; i < activePlayers.size(); i++) {
            activePlayers.get(i).active = (i < advanceCount);
        }

        if (gameState.equals("ROUND 3")) {
            showTopMovies(roundMovies);
        }

        if (nextState.equals("WINNER")) {
            Player winner = activePlayers.get(0);
            oscarWinners.add("Year " + currentYear + ": " + winner.name + " (₹" + winner.earnings + ")");
            currentYear++;
        }

        gameState = nextState;
        updateUI();
    }

    private void showTopMovies(List<MovieRecord> movies) {
        Collections.sort(movies, (a, b) -> Integer.compare(b.earnings, a.earnings));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, movies.size()); i++) {
            MovieRecord m = movies.get(i);
            sb.append("#").append(i + 1).append(" ").append(m.playerName).append(": ₹").append(m.earnings).append("\n");
        }
        topMoviesText.setText(sb.toString());
        topMoviesSection.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        yearBadge.setText("Year " + currentYear);
        
        if (gameState.equals("WINNER")) {
            Player winner = null;
            for (Player p : players) if (p.active) winner = p;
            statsText.setText("🏆 OSCAR WINNER: " + winner.name + "\nFinal Balance: ₹" + winner.balance);
            actionButton.setText("Start Next Year");
            return;
        }

        actionButton.setText(gameState.equals("START") ? "Start Game" : "Next Round (" + gameState + ")");

        StringBuilder sb = new StringBuilder();
        List<Player> sorted = new ArrayList<>(players);
        Collections.sort(sorted, (a, b) -> Integer.compare(b.balance, a.balance));

        sb.append(String.format("%-15s | %-5s | %-5s | %-5s\n", "Name", "Loan", "Earn", "Bal"));
        sb.append("------------------------------------------\n");
        for (Player p : sorted) {
            sb.append(String.format("%-15s | %-5d | %-5d | %-5d %s\n", 
                p.name, p.loan, p.earnings, p.balance, p.active ? "★" : ""));
        }

        if (!oscarWinners.isEmpty()) {
            sb.append("\n🏆 OSCAR HALL OF FAME\n");
            for (int i = oscarWinners.size() - 1; i >= 0; i--) sb.append(oscarWinners.get(i)).append("\n");
        }

        statsText.setText(sb.toString());
    }

    private static class Player {
        String name;
        int loan, earnings, balance, lastEarnings;
        boolean active = true;
        Player(String name, int loan) {
            this.name = name; this.loan = loan; this.balance = -loan;
        }
    }

    private static class MovieRecord {
        String playerName;
        int earnings;
        MovieRecord(String name, int earn) { this.playerName = name; this.earnings = earn; }
    }
}