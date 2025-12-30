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

public class AchievementsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        TextView achievementsText = findViewById(R.id.achievementsText);
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 ACHIEVEMENTS & MILESTONES 🏆\n\n");

        try {
            File file = new File(getFilesDir(), "save_data.json");
            if (file.exists()) {
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

                    for (PlayerStats stats : allStats.values()) {
                        if (!stats.achievements.isEmpty()) {
                            sb.append("👤 ").append(stats.playerName).append("\n");
                            for (String achievement : stats.achievements) {
                                sb.append("  ✓ ").append(achievement).append("\n");
                            }
                            sb.append("\n");
                        }
                    }
                }
            }
            if (sb.toString().equals("🏆 ACHIEVEMENTS & MILESTONES 🏆\n\n")) {
                sb.append("No achievements unlocked yet. Play more rounds!");
            }
        } catch (Exception e) {
            sb.append("Error loading data.");
        }
        achievementsText.setText(sb.toString());
    }
}
