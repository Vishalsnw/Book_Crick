package com.bollywood.simulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class StockMarket implements Serializable {
    public static class SharePrice implements Serializable {
        public String producerName;
        public float currentPrice;
        public float lastPrice;
        public float bid;
        public float ask;
        public List<Float> priceHistory = new ArrayList<>();

        public SharePrice(String name, float initialPrice) {
            this.producerName = name;
            this.currentPrice = initialPrice;
            this.lastPrice = initialPrice;
            this.bid = initialPrice - 0.5f;
            this.ask = initialPrice + 0.5f;
            this.priceHistory.add(initialPrice);
        }
    }

    public List<SharePrice> stocks = new ArrayList<>();
    public List<String> tradeLogs = new ArrayList<>();
    public float industryIndex = 1000f;
    public float lastIndex = 1000f;
    private Random random = new Random();

    public void updateMarket(List<MainActivity.Player> players) {
        if (players == null) return;
        
        float totalMarketCap = 0;
        for (MainActivity.Player p : players) {
            SharePrice stock = findStock(p.name);
            if (stock == null) {
                stock = new SharePrice(p.name, 100f + random.nextInt(50));
                stocks.add(stock);
            }

            stock.lastPrice = stock.currentPrice;
            
            // Advanced bot logic: Sentiment based on balance, earnings, and star rating level
            // Buy if: High Balance (>500), Recent Hit (lastEarnings > 50), High Star Level
            // Sell if: Debt (balance < -300), Recent Flop (lastEarnings < 20)
            float balanceFactor = p.balance / 1000f;
            float earningsFactor = (p.lastEarnings - 50f) / 50f;
            float starFactor = (p.currentStar != null ? (float)p.currentStar.level : 0f) / 2.0f;
            
            float sentiment = balanceFactor + earningsFactor + starFactor;
            
            // Debt penalty: Bots panic sell if debt is high
            if (p.balance < -500) sentiment -= 3.0f;
            
            float volatility = 0.5f + (random.nextFloat() * 1.5f);
            
            // Dynamic Bid/Ask spread based on volatility
            float spread = 0.05f + (random.nextFloat() * 0.3f * volatility);
            stock.bid = Math.max(1f, stock.currentPrice - spread);
            stock.ask = stock.currentPrice + spread;

            // Simulated trade execution with momentum and 300+ bot swarm behavior
            float randomNoise = (random.nextFloat() * 2f - 1f);
            float move = (sentiment * 0.5f + randomNoise) * volatility;
            
            stock.currentPrice = Math.max(5f, stock.currentPrice + move);
            stock.lastPrice = stock.currentPrice; // Update LTP to current price after trade
            stock.priceHistory.add(stock.currentPrice);
            if (stock.priceHistory.size() > 20) stock.priceHistory.remove(0);

            // Sort stocks by current price (High value at top)
            java.util.Collections.sort(stocks, (a, b) -> Float.compare(b.currentPrice, a.currentPrice));

            // Logging significant movements (50-300+ bots swarm)
            if (Math.abs(move) > 1.2f) {
                String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                String action = move > 0 ? "BOUGHT" : "SOLD";
                int activeBots = 100 + random.nextInt(250); // 100 to 350 active traders
                String reason = move > 0 ? (p.lastEarnings > 60 ? "HIT STREAK" : "CASH GROWTH") : (p.balance < 0 ? "DEBT PANIC" : "MARKET EXIT");
                tradeLogs.add(0, String.format("[%s] %d BOTS %s %s (%s) @ ₹%.2f", 
                    time, activeBots, action, p.name, reason, stock.currentPrice));
            }
            totalMarketCap += stock.currentPrice;
        }
        
        lastIndex = industryIndex;
        industryIndex = (totalMarketCap / Math.max(1, stocks.size())) * 10;
        
        if (tradeLogs.size() > 100) tradeLogs = tradeLogs.subList(0, 100);
    }

    private SharePrice findStock(String name) {
        for (SharePrice s : stocks) {
            if (s.producerName.equals(name)) return s;
        }
        return null;
    }
}