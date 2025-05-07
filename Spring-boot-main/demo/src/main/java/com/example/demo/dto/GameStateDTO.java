package com.example.demo.dto;

import java.util.HashMap;
import java.util.Map;

public class GameStateDTO {
    private String currentRoom;
    private Map<String, Integer> monsterHpMap = new HashMap<>();
    private boolean gameOver = false;  // 添加 gameOver 欄位

    // --- Getters and Setters ---
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }

    public Map<String, Integer> getMonsterHpMap() { return monsterHpMap; }
    public void setMonsterHpMap(Map<String, Integer> monsterHpMap) {
        if (monsterHpMap != null) {
            this.monsterHpMap = monsterHpMap;
        } else {
            this.monsterHpMap = new HashMap<>();
        }
    }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; } // 添加 setGameOver
}
