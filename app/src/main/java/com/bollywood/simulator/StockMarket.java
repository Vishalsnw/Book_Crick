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
            
            // High-frequency simulation of 300+ bots
            float sentiment = (p.balance / 1000f) + (p.lastEarnings / 100f);
            float volatility = 0.5f + random.nextFloat();
            
            // Generate Bid/Ask spread
            float spread = 0.1f + random.nextFloat() * 0.4f;
            stock.bid = Math.max(1f, stock.currentPrice - spread);
            stock.ask = stock.currentPrice + spread;

            // Simulated trade execution
            float move = (sentiment + (random.nextFloat() * 2f - 1f)) * volatility;
            stock.currentPrice = Math.max(5f, stock.currentPrice + move);
            stock.priceHistory.add(stock.currentPrice);
            if (stock.priceHistory.size() > 20) stock.priceHistory.remove(0);

            if (Math.abs(move) > 2.0f) {
                String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
                String type = move > 0 ? "BOUGHT" : "SOLD";
                tradeLogs.add(0, String.format("[%s] BOT_X%03d %s %s @ %.2f", 
                    time, random.nextInt(300), type, p.name, stock.currentPrice));
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