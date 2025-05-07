package com.example.demo.command;

import com.example.demo.model.Player;
import com.example.demo.model.Monster;

import java.util.List;

public class SkillResult implements CommandResult {
    private List<String> messages;
    private Player player;
    private Monster monster;
    private boolean monsterDefeated;
    private boolean gameWin;

    public SkillResult(List<String> messages, Player player, Monster monster, 
                      boolean monsterDefeated, boolean gameWin) {
        this.messages = messages;
        this.player = player;
        this.monster = monster;
        this.monsterDefeated = monsterDefeated;
        this.gameWin = gameWin;
    }

    // 加入 getter 方法
    public List<String> getMessages() {
        return messages;
    }

    public Player getPlayer() {
        return player;
    }
    
    public Monster getMonster() {
        return monster;
    }
    
    public boolean isMonsterDefeated() {
        return monsterDefeated;
    }
    
    public boolean isGameWin() {
        return gameWin;
    }
}
