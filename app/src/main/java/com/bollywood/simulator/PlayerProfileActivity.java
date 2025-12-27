package com.bollywood.simulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PlayerProfileActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "BollywoodPrefs";
    private static final String KEY_PLAYER_STATS = "PlayerStats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        String playerName = getIntent().getStringExtra("playerName");
        TextView profileText = findViewById(R.id.profileText);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String statsJson = prefs.getString(KEY_PLAYER_STATS, null);

        StringBuilder sb = new StringBuilder();
        sb.append("👤 PLAYER PROFILE\n\n");
        sb.append("Player: ").append(playerName != null ? playerName : "Unknown").append("\n\n");

        if (statsJson != null && playerName != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, PlayerStats>>() {}.getType();
            Map<String, PlayerStats> allStats = gson.fromJson(statsJson, type);
            PlayerStats stats = allStats.get(playerName);

            if (stats != null) {
                sb.append("📊 CAREER STATISTICS\n");
                sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                sb.append("Total Movies: ").append(stats.totalMovies).append("\n");
                sb.append("Total Earnings: ₹").append(stats.totalEarnings).append("\n");
                sb.append("Oscar Wins: ").append(stats.oscarWins).append("\n");
                sb.append("Bankruptcies: ").append(stats.bankruptcies).append("\n");
                sb.append("Longest Win Streak: ").append(stats.longestWinStreak).append("\n");
                sb.append("Retired: ").append(stats.retired ? "Yes" : "No").append("\n");
                
                if (!stats.achievements.isEmpty()) {
                    sb.append("\n🏅 ACHIEVEMENTS\n");
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    for (String achievement : stats.achievements) {
                        sb.append("✓ ").append(achievement).append("\n");
                    }
                }
                
                if (!stats.movieHistory.isEmpty()) {
                    sb.append("\n🎬 RECENT MOVIES\n");
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    int start = Math.max(0, stats.movieHistory.size() - 5);
                    for (int i = stats.movieHistory.size() - 1; i >= start; i--) {
                        Movie m = stats.movieHistory.get(i);
                        sb.append("Year ").append(m.year).append(", Round ").append(m.round).append(": ")
                          .append(m.title).append(" - ₹").append(m.earnings).append("\n");
                    }
                }
            } else {
                sb.append("No profile data yet.");
            }
        } else {
            sb.append("No player data available.");
        }

        profileText.setText(sb.toString());
    }
}
