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

public class AchievementsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "BollywoodPrefs";
    private static final String KEY_PLAYER_STATS = "PlayerStats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        TextView achievementsText = findViewById(R.id.achievementsText);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String statsJson = prefs.getString(KEY_PLAYER_STATS, null);

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 ACHIEVEMENTS & MILESTONES 🏆\n\n");

        if (statsJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, PlayerStats>>() {}.getType();
            Map<String, PlayerStats> allStats = gson.fromJson(statsJson, type);

            for (PlayerStats stats : allStats.values()) {
                if (!stats.achievements.isEmpty()) {
                    sb.append("👤 ").append(stats.playerName).append("\n");
                    for (String achievement : stats.achievements) {
                        sb.append("  ✓ ").append(achievement).append("\n");
                    }
                    sb.append("\n");
                }
            }

            if (sb.toString().equals("🏆 ACHIEVEMENTS & MILESTONES 🏆\n\n")) {
                sb.append("No achievements unlocked yet. Play more rounds!");
            }
        } else {
            sb.append("No data available yet.");
        }

        achievementsText.setText(sb.toString());
    }
}
