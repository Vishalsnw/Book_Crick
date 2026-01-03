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
    private float baseMarketCap = 0f;
    private transient Random random = new Random();

    public StockMarket() {
        random = new Random();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        random = new Random();
    }

    public enum MarketEvent {
        NORMAL("Stable Market", 1.0f),
        BULL_RUN("Bull Run! Prices Soaring", 1.5f),
        BEAR_CRASH("Market Crash! Panic Selling", 0.6f),
        CENSORSHIP_SCANDAL("Censorship Row! Stocks Dip", 0.8f),
        TAX_BREAK("Tax Break for Cinema! Growth", 1.2f);

        public final String description;
        public final float multiplier;
        MarketEvent(String desc, float mult) { this.description = desc; this.multiplier = mult; }
    }

    public MarketEvent currentEvent = MarketEvent.NORMAL;

    public void updateMarket(List<MainActivity.Player> players, GameEngine.IndustryTrend trend) {
        if (players == null) return;
        
        // Randomly trigger market events
        if (random.nextFloat() < 0.15f) {
            currentEvent = MarketEvent.values()[random.nextInt(MarketEvent.values().length)];
            tradeLogs.add(0, "🚨 MARKET ALERT: " + currentEvent.description);
        } else {
            currentEvent = MarketEvent.NORMAL;
        }

        float totalMarketCap = 0;
        boolean isFirstInitialization = stocks.isEmpty();

        for (MainActivity.Player p : players) {
            SharePrice stock = findStock(p.name);
            if (stock == null) {
                // Companies start with a nominal price of 100
                stock = new SharePrice(p.name, 100f);
                stocks.add(stock);
            }

            stock.lastPrice = stock.currentPrice;
            
            // Smarter bot logic: Weighted factors and momentum
            float balanceFactor = p.balance / 1000f;
            float earningsFactor = (p.lastEarnings - 40f) / 60f;
            float starFactor = (p.currentStar != null ? (float)p.currentStar.level : 0f) / 4.0f;
            
            // Sentiment tracking
            float momentum = 0f;
            if (p.lastEarnings > 80) momentum = 0.3f; 
            else if (p.lastEarnings < 20) momentum = -0.3f;

            // Bots start neutral and decide based on performance
            // Force buying at the very beginning to ensure market starts active
            float initialBuyForce = (isFirstInitialization) ? 0.8f : 0f;
            float sentiment = (balanceFactor + earningsFactor + starFactor + momentum + initialBuyForce) * currentEvent.multiplier;
            
            if (p.balance < -500) sentiment -= 2.0f;
            
            float volatility = 0.3f + (random.nextFloat() * 0.8f);
            float spread = 0.05f + (random.nextFloat() * 0.2f * volatility);
            stock.bid = Math.max(1f, stock.currentPrice - spread);
            stock.ask = stock.currentPrice + spread;

            // Price movements are bot-driven based on performance sentiment
            float randomNoise = (random.nextFloat() * 1.0f - 0.5f);
            float move = (sentiment * 0.2f + randomNoise) * volatility;
            
            // Apply move
            stock.currentPrice = Math.max(10f, stock.currentPrice + move);
            stock.priceHistory.add(stock.currentPrice);
            if (stock.priceHistory.size() > 20) stock.priceHistory.remove(0);

            // Log more activity (lower threshold)
            if (Math.abs(move) > 0.5f) { 
                String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                String action = move > 0 ? "BOUGHT" : "SOLD";
                int activeBots = 50 + random.nextInt(150);
                String reason = isFirstInitialization ? "IPO ENTRY" : (move > 0 ? "GOOD RESULTS" : "PERFORMANCE DIP");
                tradeLogs.add(0, String.format("[%s] %d BOTS %s %s (%s) @ ₹%.2f", 
                    time, activeBots, action, p.name, reason, stock.currentPrice));
            }
            totalMarketCap += stock.currentPrice;
        }
        
        java.util.Collections.sort(stocks, (a, b) -> Float.compare(b.currentPrice, a.currentPrice));
        
        lastIndex = industryIndex;
        
        // Proper Index Logic: Start at 1000 and track growth relative to initial market cap
        if (isFirstInitialization || baseMarketCap == 0) {
            baseMarketCap = totalMarketCap;
            industryIndex = 1000f;
        } else {
            // Index represents relative change from 1000 base
            industryIndex = (totalMarketCap / baseMarketCap) * 1000f;
        }
        
        if (tradeLogs.size() > 100) tradeLogs = tradeLogs.subList(0, 100);
    }

    private SharePrice findStock(String name) {
        for (SharePrice s : stocks) {
            if (s.producerName.equals(name)) return s;
        }
        return null;
    }
}
