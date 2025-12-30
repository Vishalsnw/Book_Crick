package com.bollywood.simulator;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class PlayerProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        String playerName = getIntent().getStringExtra("playerName");
        TextView profileText = findViewById(R.id.profileText);

        StringBuilder sb = new StringBuilder();
        sb.append("👤 PLAYER PROFILE\n\n");
        sb.append("Player: ").append(playerName != null ? playerName : "Unknown").append("\n\n");

        try {
            File file = new File(getFilesDir(), "save_data.json");
            if (file.exists() && playerName != null) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                String json = new String(data, "UTF-8");
                Gson gson = new Gson();
                Map<String, Object> saveData = gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
                
                if (saveData.containsKey("player_stats")) {
                    Map<String, PlayerStats> allStats = gson.fromJson(gson.toJson(saveData.get("player_stats")), 
                        new TypeToken<HashMap<String, PlayerStats>>(){}.getType());
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
                }
            } else {
                sb.append("No data found.");
            }
        } catch (Exception e) {
            sb.append("Error loading data.");
        }

        profileText.setText(sb.toString());
    }
}
