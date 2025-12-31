package com.bollywood.simulator;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerIncomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_income);

        TextView incomeListText = findViewById(R.id.incomeListText);
        
        Object playersObj = getIntent().getSerializableExtra("players");
        ArrayList<MainActivity.Player> players = null;
        
        if (playersObj instanceof ArrayList) {
            players = (ArrayList<MainActivity.Player>) playersObj;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("💰 PLAYER INCOME REPORT 💰\n\n");
        
        if (players != null && !players.isEmpty()) {
            List<MainActivity.Player> sorted = new ArrayList<>(players);
            Collections.sort(sorted, (a, b) -> Integer.compare(b.earnings, a.earnings));
            
            sb.append(String.format("%-2s | %-12s | %4s | %4s | %4s | %s | %s\n", "R", "Player", "Loan", "Earn", "Bal", "🏆", "Stat"));
            sb.append("=========================================================\n");
            
            for (int i = 0; i < sorted.size(); i++) {
                MainActivity.Player p = sorted.get(i);
                String rank = String.format("%02d", i + 1);
                String status = p.active ? "★" : "✗";
                String name = p.name.length() > 12 ? p.name.substring(0, 10) + ".." : p.name;
                
                // Get oscar wins for this player from stats
                int oscars = 0;
                // Since we don't have easy access to playerStats here without passing it, 
                // we'll rely on the player object if we add the field there, or just show 0 for now.
                // Better approach: MainActivity.Player should have an oscarWins field.
                
                sb.append(String.format("%-2s | %-12s | %4d | %4d | %4d | %2d | %s\n", 
                    rank, name, p.loan, p.earnings, p.balance, p.oscarWins, status));
            }
            
            sb.append("\n📊 STATISTICS:\n");
            int totalEarnings = players.stream().mapToInt(p -> p.earnings).sum();
            int totalLoans = players.stream().mapToInt(p -> p.loan).sum();
            int activePlayers = (int) players.stream().filter(p -> p.active).count();
            
            sb.append("Total Players: ").append(players.size()).append("\n");
            sb.append("Active Players: ").append(activePlayers).append("\n");
            sb.append("Total Earnings: ₹").append(totalEarnings).append("\n");
            sb.append("Total Loans: ₹").append(totalLoans).append("\n");
            sb.append("Average Earnings: ₹").append(totalEarnings / players.size()).append("\n");
        } else {
            sb.append("No player data available. Start a game first!");
        }
        
        incomeListText.setText(sb.toString());
    }
}
