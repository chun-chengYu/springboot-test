package com.example.demo.command;

import com.example.demo.model.Player;
import com.example.demo.model.Monster;

import java.util.List;

public class AttackResult implements CommandResult {
    private List<String> messages;
    private Player player;
    private Monster monster;
    private boolean monsterDefeated;
    private boolean gameWin;

    public AttackResult(List<String> messages, Player player, Monster monster, boolean monsterDefeated, boolean gameWin) {
        this.messages = messages;
        this.player = player;
        this.monster = monster;
        this.monsterDefeated = monsterDefeated;
        this.gameWin = gameWin;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public boolean isMonsterDefeated() {
        return monsterDefeated;
    }

    public void setMonsterDefeated(boolean monsterDefeated) {
        this.monsterDefeated = monsterDefeated;
    }

    public boolean isGameWin() {
        return gameWin;
    }

    public void setGameWin(boolean gameWin) {
        this.gameWin = gameWin;
    }
}
