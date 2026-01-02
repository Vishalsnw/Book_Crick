package com.bollywood.simulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StockMarket implements Serializable {
    public static class SharePrice implements Serializable {
        public String producerName;
        public float currentPrice;
        public float lastPrice;
        public List<Float> priceHistory = new ArrayList<>();

        public SharePrice(String name, float initialPrice) {
            this.producerName = name;
            this.currentPrice = initialPrice;
            this.lastPrice = initialPrice;
            this.priceHistory.add(initialPrice);
        }
    }

    public List<SharePrice> stocks = new ArrayList<>();
    public List<String> tradeLogs = new ArrayList<>();
    public float industryIndex = 1000f;
    public float lastIndex = 1000f;
    public int botCount = 300;
    private Random random = new Random();

    public void updateMarket(List<MainActivity.Player> players) {
        float totalTopPrice = 0;
        int count = 0;
        
        for (MainActivity.Player p : players) {
            SharePrice stock = findStock(p.name);
            if (stock == null) {
                stock = new SharePrice(p.name, 100f);
                stocks.add(stock);
            }

            stock.lastPrice = stock.currentPrice;
            
            // Simulation of 300 bots trading
            float netDemand = 0;
            int buys = 0;
            int sells = 0;
            
            for (int i = 0; i < botCount; i++) {
                // Bots buy if player has profit and high balance, sell if in debt
                float sentiment = (p.balance / 500f) + (p.lastEarnings / 50f) + (random.nextFloat() * 2f - 1f);
                if (sentiment > 1.2f) {
                    netDemand += 0.05f;
                    buys++;
                } else if (sentiment < -0.8f) {
                    netDemand -= 0.07f;
                    sells++;
                }
            }
            
            float change = netDemand + (random.nextFloat() * 2f - 1f);
            stock.currentPrice = Math.max(5f, stock.currentPrice + change);
            stock.priceHistory.add(stock.currentPrice);
            if (stock.priceHistory.size() > 20) stock.priceHistory.remove(0);

            if (buys > 50 || sells > 50) {
                String action = buys > sells ? "BUYING" : "SELLING";
                tradeLogs.add(0, String.format("[%s] Bot swarm %s %s stock at %.2f", 
                    new java.util.Date().toString().substring(11, 19), action, p.name, stock.currentPrice));
            }
        }
        
        // Calculate BIX (Bollywood Index) - Average of top 10 stocks
        stocks.sort((a, b) -> Float.compare(b.currentPrice, a.currentPrice));
        for (int i = 0; i < Math.min(10, stocks.size()); i++) {
            totalTopPrice += stocks.get(i).currentPrice;
            count++;
        }
        
        lastIndex = industryIndex;
        if (count > 0) {
            industryIndex = (totalTopPrice / count) * 10; // Scaled index
        }
        
        if (tradeLogs.size() > 50) tradeLogs = tradeLogs.subList(0, 50);
    }

    private SharePrice findStock(String name) {
        for (SharePrice s : stocks) {
            if (s.producerName.equals(name)) return s;
        }
        return null;
    }
}
