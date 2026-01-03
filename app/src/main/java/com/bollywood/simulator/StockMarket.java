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
        for (MainActivity.Player p : players) {
            SharePrice stock = findStock(p.name);
            if (stock == null) {
                stock = new SharePrice(p.name, 100f + random.nextInt(50));
                stocks.add(stock);
            }

            stock.lastPrice = stock.currentPrice;
            
            // Smarter bot logic: Weighted factors and momentum
            float balanceFactor = p.balance / 1000f;
            float earningsFactor = (p.lastEarnings - 50f) / 50f;
            float starFactor = (p.currentStar != null ? (float)p.currentStar.level : 0f) / 2.0f;
            
            // Smarter Sentiment: Considering cumulative performance (momentum)
            float momentum = 0f;
            if (p.lastEarnings > 80) momentum = 0.5f; // Hit momentum
            else if (p.lastEarnings < 20) momentum = -0.5f; // Flop momentum

            // Advanced AI Strategy: Value investing vs Trend following
            // High value (low price but high balance) vs Growth (high earnings)
            float valueFactor = (p.balance > 500 && stock.currentPrice < 80) ? 0.8f : 0f;
            
            float sentiment = (balanceFactor + earningsFactor + starFactor + momentum + valueFactor) * currentEvent.multiplier;
            
            // Genre Trend Impact
            if (p.lastEarnings > 60 && trend != null) {
                sentiment += 0.6f; // Stronger trend following
            }

            // Bots now anticipate future performance: "Star in the making"
            if (p.nominationCount > 2 && p.oscarWins == 0) {
                sentiment += 0.4f; // Speculation on upcoming Oscar win
            }
            
            // Dividend Logic: If cash > 1000, pay 5% dividend to boost stock price
            if (p.balance > 1000) {
                float dividend = p.balance * 0.05f;
                p.balance -= dividend;
                sentiment += 1.0f; // High sentiment boost for dividend paying stocks
                tradeLogs.add(0, String.format("💰 DIVIDEND: %s paid ₹%.0f to shareholders!", p.name, dividend));
            }

            // Hostile Takeover / Acquisition Logic
            if (p.balance < -1000 && stock.currentPrice < 30) {
                float bailout = 500f;
                p.balance += bailout;
                stock.currentPrice *= 0.5f; // Price tanks further due to dilution
                tradeLogs.add(0, String.format("⚠️ ACQUISITION: %s bailed out by Mega-Corp!", p.name));
            }

            if (p.balance < -500) sentiment -= 3.0f;
            
            float volatility = 0.5f + (random.nextFloat() * 1.5f);
            float spread = 0.05f + (random.nextFloat() * 0.3f * volatility);
            stock.bid = Math.max(1f, stock.currentPrice - spread);
            stock.ask = stock.currentPrice + spread;

            float randomNoise = (random.nextFloat() * 2f - 1f);
            float move = (sentiment * 0.5f + randomNoise) * volatility;
            
            stock.currentPrice = Math.max(5f, stock.currentPrice + move);
            stock.lastPrice = stock.currentPrice;
            stock.priceHistory.add(stock.currentPrice);
            if (stock.priceHistory.size() > 20) stock.priceHistory.remove(0);

            if (Math.abs(move) > 1.2f) {
                String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                String action = move > 0 ? "BOUGHT" : "SOLD";
                int activeBots = 100 + random.nextInt(250);
                String reason = move > 0 ? (p.lastEarnings > 60 ? "HIT STREAK" : "CASH GROWTH") : (p.balance < 0 ? "DEBT PANIC" : "MARKET EXIT");
                tradeLogs.add(0, String.format("[%s] %d BOTS %s %s (%s) @ ₹%.2f", 
                    time, activeBots, action, p.name, reason, stock.currentPrice));
            }
            totalMarketCap += stock.currentPrice;
        }
        
        java.util.Collections.sort(stocks, (a, b) -> Float.compare(b.currentPrice, a.currentPrice));
        
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