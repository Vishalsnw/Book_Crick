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

        stockMarket = (StockMarket) getIntent().getSerializableExtra("stockMarket");

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Simulate real-time bot trading inside the UI
                if (stockMarket != null) {
                    // Internal simulation for visual "terminal" effect
                    simulateLiveTicks();
                    updateUI();
                }
                handler.postDelayed(this, 500); // 500ms for "high frequency" feel
            }
        };
        handler.post(updateRunnable);

        backButton.setOnClickListener(v -> finish());
    }

    private void simulateLiveTicks() {
        if (stockMarket.stocks.isEmpty()) return;
        // Minor random fluctuations to simulate active order book
        for (StockMarket.SharePrice s : stockMarket.stocks) {
            float noise = (float)(Math.random() * 0.2 - 0.1);
            s.bid = Math.max(1f, s.bid + noise);
            s.ask = Math.max(s.bid + 0.1f, s.ask + noise);
        }
    }

    private void updateUI() {
        if (stockMarket == null) return;

        float diff = stockMarket.industryIndex - stockMarket.lastIndex;
        String color = diff >= 0 ? "#00FF00" : "#FF0000";
        String sign = diff >= 0 ? "+" : "";

        indexValue.setText(String.format("INDEX: %.2f (%s%.2f)", stockMarket.industryIndex, sign, diff));
        indexValue.setTextColor(android.graphics.Color.parseColor(color));

        StringBuilder sb = new StringBuilder();
        // Show Terminal-style Quote Board
        sb.append(String.format("%-10s | %8s | %8s | %8s\n", "SYMBOL", "BID", "ASK", "LTP"));
        sb.append("--------------------------------------------\n");
        
        List<StockMarket.SharePrice> displayList = stockMarket.stocks;
        for (int i = 0; i < Math.min(15, displayList.size()); i++) {
            StockMarket.SharePrice s = displayList.get(i);
            String name = s.producerName;
            if (name.length() > 8) name = name.substring(0, 8);
            
            sb.append(String.format("%-10s | %8.2f | %8.2f | %8.2f\n", 
                name, s.bid, s.ask, s.currentPrice));
        }

        sb.append("\n--- RECENT TRADES ---\n");
        List<String> logs = stockMarket.tradeLogs;
        if (logs != null) {
            for (int i = 0; i < Math.min(20, logs.size()); i++) {
                sb.append(logs.get(i)).append("\n");
            }
        }
        tradingLogs.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}