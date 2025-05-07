package com.example.demo.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerSaveRequestDTO {
    private Long id;
    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    private int level;
    private List<String> skillNames;
    private Set<String> usedSkills;
    private int totalDamage;
    private int killCount;
    private Map<String, Integer> inventory;
    private GameStateDTO gameState; // 使用一個專門的 GameState DTO

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public List<String> getSkillNames() { return skillNames; }
    public void setSkillNames(List<String> skillNames) { this.skillNames = skillNames; }
    public Set<String> getUsedSkills() { return usedSkills; }
    public void setUsedSkills(Set<String> usedSkills) { this.usedSkills = usedSkills; }
    public int getTotalDamage() { return totalDamage; }
    public void setTotalDamage(int totalDamage) { this.totalDamage = totalDamage; }
    public int getKillCount() { return killCount; }
    public void setKillCount(int killCount) { this.killCount = killCount; }
    public Map<String, Integer> getInventory() { return inventory; }
    public void setInventory(Map<String, Integer> inventory) { this.inventory = inventory; }
    public GameStateDTO getGameState() { return gameState; }
    public void setGameState(GameStateDTO gameState) { this.gameState = gameState; }
}
