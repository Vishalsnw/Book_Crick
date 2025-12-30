package com.bollywood.simulator;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OscarListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oscar_list);

        TextView oscarListText = findViewById(R.id.oscarListText);
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 OSCAR HALL OF FAME 🏆\n\n");

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
                
                if (saveData.containsKey("oscar_winners")) {
                    List<String> oscarWinners = gson.fromJson(gson.toJson(saveData.get("oscar_winners")), new TypeToken<ArrayList<String>>(){}.getType());
                    if (!oscarWinners.isEmpty()) {
                        for (int i = oscarWinners.size() - 1; i >= 0; i--) {
                            sb.append(oscarWinners.get(i)).append("\n");
                        }
                    } else {
                        sb.append("No winners yet.");
                    }
                }
            } else {
                sb.append("No data found.");
            }
        } catch (Exception e) {
            sb.append("Error loading data: ").append(e.getMessage());
        }
        
        oscarListText.setText(sb.toString());
    }
}
