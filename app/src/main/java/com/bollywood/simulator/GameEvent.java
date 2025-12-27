package com.bollywood.simulator;

import java.io.Serializable;

public class GameEvent implements Serializable {
    public String playerName;
    public String eventType;
    public String description;
    public int impact;
    public int round;
    public int year;

    public GameEvent(String playerName, String eventType, String description, int impact, int round, int year) {
        this.playerName = playerName;
        this.eventType = eventType;
        this.description = description;
        this.impact = impact;
        this.round = round;
        this.year = year;
    }
}
