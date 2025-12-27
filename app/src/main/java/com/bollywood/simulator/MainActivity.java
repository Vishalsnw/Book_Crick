package com.bollywood.simulator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private String[] PLAYER_NAMES = {
        "Golu", "Amit Bagle", "Mangesh", "Vasim", "Amit Randhe", "Khushi", "Ajinkya", "Vinay",
        "Aashish", "Ashok Singh", "Sandip Basra", "Gokul", "Ritesh", "Bipin", "Ajit Bonde", "Amol Patil",
        "Hemant", "Ravi Patil", "Sachin Pardesi", "Sachin Patil", "Vishal", "Nitin", "Dipak Trivedi",
        "Sunil", "Charu", "Bhavesh Chaudhari", "Dipak R", "Mayur", "Nilesh", "Dipak BH", "Sunil"
    };

    private List<Player> players = new ArrayList<>();
    private List<String> oscarWinners = new ArrayList<>();
    private int currentYear = 1;
    private String gameState = "START"; // START, ROUND1, ROUND2, ROUND3, SEMIFINAL, FINAL, WINNER
    private Random random = new Random();

    private TextView titleText;
    private TextView statsText;
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.titleText);
        statsText = findViewById(R.id.statsText);
        actionButton = findViewById(R.id.actionButton);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick();
            }
        });

        updateUI();
    }

    private void handleButtonClick() {
        switch (gameState) {
            case "START":
                startNewYear();
                break;
            case "ROUND1":
                playRound(16, "ROUND2");
                break;
            case "ROUND2":
                playRound(8, "ROUND3");
                break;
            case "ROUND3":
                playRound(4, "SEMIFINAL");
                break;
            case "SEMIFINAL":
                playRound(2, "FINAL");
                break;
            case "FINAL":
                playRound(1, "WINNER");
                break;
            case "WINNER":
                gameState = "START";
                updateUI();
                break;
        }
    }

    private void startNewYear() {
        players.clear();
        for (String name : PLAYER_NAMES) {
            int loan = random.nextInt(91) + 10;
            players.add(new Player(name, loan));
        }
        gameState = "ROUND1";
        updateUI();
    }

    private void playRound(int advanceCount, String nextState) {
        List<Player> activePlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.active) {
                int roundEarnings = random.nextInt(101);
                p.earnings += roundEarnings;
                p.balance = p.earnings - p.loan;
                p.lastRoundScore = roundEarnings;
                activePlayers.add(p);
            }
        }

        Collections.sort(activePlayers, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o2.lastRoundScore, o1.lastRoundScore);
            }
        });

        for (int i = 0; i < activePlayers.size(); i++) {
            activePlayers.get(i).active = (i < advanceCount);
        }

        if (nextState.equals("WINNER")) {
            Player winner = activePlayers.get(0);
            oscarWinners.add("Year " + currentYear + ": " + winner.name + " (Earnings: " + winner.earnings + ")");
            currentYear++;
        }

        gameState = nextState;
        updateUI();
    }

    private void updateUI() {
        titleText.setText("Bollywood Simulator - Year " + currentYear);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Current State: ").append(gameState).append("\n\n");

        if (gameState.equals("WINNER")) {
            Player winner = null;
            for (Player p : players) if (p.active) winner = p;
            sb.append("🏆 OSCAR WINNER 🏆\n");
            sb.append(winner.name).append("\n\n");
            actionButton.setText("Start Next Year");
        } else if (gameState.equals("START")) {
            actionButton.setText("Start New Game");
        } else {
            actionButton.setText("Next Round (" + gameState + ")");
        }

        sb.append("LIVE RANKINGS (By Balance):\n");
        List<Player> sorted = new ArrayList<>(players);
        Collections.sort(sorted, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.compare(o2.balance, o1.balance);
            }
        });

        for (Player p : sorted) {
            sb.append(p.active ? "● " : "○ ")
              .append(p.name).append(" | ")
              .append("Loan: ").append(p.loan).append(" | ")
              .append("Bal: ").append(p.balance).append("\n");
        }

        if (!oscarWinners.isEmpty()) {
            sb.append("\nOSCAR HISTORY:\n");
            for (String record : oscarWinners) {
                sb.append(record).append("\n");
            }
        }

        statsText.setText(sb.toString());
    }

    private static class Player {
        String name;
        int loan;
        int earnings;
        int balance;
        int lastRoundScore;
        boolean active;

        public Player(String name, int loan) {
            this.name = name;
            this.loan = loan;
            this.balance = -loan;
            this.active = true;
            this.earnings = 0;
        }
    }
}