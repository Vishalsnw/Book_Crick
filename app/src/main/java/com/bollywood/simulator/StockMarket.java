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
    public int botCount = 300;
    private Random random = new Random();

    public void updateMarket(List<MainActivity.Player> players) {
        for (MainActivity.Player p : players) {
            SharePrice stock = findStock(p.name);
            if (stock == null) {
                stock = new SharePrice(p.name, 100f);
                stocks.add(stock);
            }

            stock.lastPrice = stock.currentPrice;
            
            // Simulation of 300 bots trading
            float netDemand = 0;
            for (int i = 0; i < botCount; i++) {
                // Bots buy if player has profit and high balance, sell if in debt
                float sentiment = (p.balance / 500f) + (p.lastEarnings / 50f) + (random.nextFloat() * 2f - 1f);
                if (sentiment > 1.0f) netDemand += 0.1f; // Buy pressure
                else if (sentiment < -1.0f) netDemand -= 0.15f; // Sell pressure
            }
            
            float change = netDemand + (random.nextFloat() * 4f - 2f);
            stock.currentPrice = Math.max(5f, stock.currentPrice + change);
            stock.priceHistory.add(stock.currentPrice);
            if (stock.priceHistory.size() > 20) stock.priceHistory.remove(0);
        }
    }

    private SharePrice findStock(String name) {
        for (SharePrice s : stocks) {
            if (s.producerName.equals(name)) return s;
        }
        return null;
    }
}
