package com.bollywood.simulator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class LiveTradingActivity extends AppCompatActivity {
    private TextView indexValue, tradingLogs;
    private Handler handler = new Handler(Looper.getMainLooper());
    private StockMarket stockMarket;
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_trading);

        indexValue = findViewById(R.id.index_value);
        tradingLogs = findViewById(R.id.trading_logs);
        Button backButton = findViewById(R.id.back_button);

        // In a real app, we'd pass this via Intent or a Singleton
        // For this simulation, we'll assume it's available or create a mock update
        stockMarket = (StockMarket) getIntent().getSerializableExtra("stockMarket");

        updateUI();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateUI();
                handler.postDelayed(this, 2000); // Update every 2 seconds
            }
        };
        handler.post(updateRunnable);

        backButton.setOnClickListener(v -> finish());
    }

    private void updateUI() {
        if (stockMarket == null) return;

        float diff = stockMarket.industryIndex - stockMarket.lastIndex;
        float percent = (diff / stockMarket.lastIndex) * 100;
        String color = diff >= 0 ? "#00FF00" : "#FF0000";
        String sign = diff >= 0 ? "+" : "";

        indexValue.setText(String.format("%.2f (%s%.1f%%)", stockMarket.industryIndex, sign, percent));
        indexValue.setTextColor(android.graphics.Color.parseColor(color));

        StringBuilder sb = new StringBuilder();
        List<String> logs = stockMarket.tradeLogs;
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        tradingLogs.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}