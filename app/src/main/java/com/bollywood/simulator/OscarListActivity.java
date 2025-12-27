package com.bollywood.simulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OscarListActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "BollywoodPrefs";
    private static final String KEY_OSCARS = "OscarWinners";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oscar_list);

        TextView oscarListText = findViewById(R.id.oscarListText);
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String oscarsJson = prefs.getString(KEY_OSCARS, null);
        
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 OSCAR HALL OF FAME 🏆\n\n");
        
        if (oscarsJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> oscarWinners = gson.fromJson(oscarsJson, type);
            
            if (!oscarWinners.isEmpty()) {
                for (int i = oscarWinners.size() - 1; i >= 0; i--) {
                    sb.append(oscarWinners.get(i)).append("\n");
                }
            } else {
                sb.append("No winners yet. Start playing to make history!");
            }
        } else {
            sb.append("No winners yet. Start playing to make history!");
        }
        
        oscarListText.setText(sb.toString());
    }
}
