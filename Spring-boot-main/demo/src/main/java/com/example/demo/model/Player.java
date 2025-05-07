package com.example.demo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.io.IOException;
import java.util.*;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    // private int level = 1; // <--- 刪掉這行
    private int totalDamage = 0;
    private int killCount = 0;

    private String currentRoomId;

    @Lob
    @Column(name = "monster_hp_map_json", columnDefinition = "TEXT")
    private String monsterHpMapJson;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_skills", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "skill_name")
    private List<String> skillNames = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_used_skills", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "used_skill_name")
    private Set<String> usedSkills = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_inventory", joinColumns = @JoinColumn(name = "player_id"))
    @MapKeyColumn(name = "item_name")
    @Column(name = "quantity")
    private Map<String, Integer> inventory = new HashMap<>();

    @Transient
    private ObjectMapper objectMapper = new ObjectMapper();

    public Player() {}

    public void initialize(String name, int hp, int attack, List<String> defaultSkillNames, String initialRoomId) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.skillNames.clear();
        if (defaultSkillNames != null) {
            this.skillNames.addAll(defaultSkillNames);
        }
        this.currentRoomId = initialRoomId;
        try {
            this.monsterHpMapJson = objectMapper.writeValueAsString(new HashMap<String, Integer>());
        } catch (JsonProcessingException e) {
            this.monsterHpMapJson = "{}";
        }
    }

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
    // public int getLevel() { return level; }  // <--- 刪掉這行
    // public void setLevel(int level) { this.level = level; } // <--- 刪掉這行
    public int getTotalDamage() { return totalDamage; }
    public void setTotalDamage(int totalDamage) { this.totalDamage = totalDamage; }
    public int getKillCount() { return killCount; }
    public void setKillCount(int killCount) { this.killCount = killCount; }
    public String getCurrentRoomId() { return currentRoomId; }
    public void setCurrentRoomId(String currentRoomId) { this.currentRoomId = currentRoomId; }

    public List<String> getSkillNames() { return skillNames; }
    public void setSkillNames(List<String> skillNames) {
        this.skillNames.clear();
        if (skillNames != null) { this.skillNames.addAll(skillNames); }
    }

    public Set<String> getUsedSkills() { return usedSkills; }
    public void setUsedSkills(Set<String> usedSkills) {
        this.usedSkills.clear();
        if (usedSkills != null) { this.usedSkills.addAll(usedSkills); }
    }

    public Map<String, Integer> getInventory() { return inventory; }
    public void setInventory(Map<String, Integer> inventory) {
        this.inventory.clear();
        if (inventory != null) { this.inventory.putAll(inventory); }
    }

    @Transient
    public Map<String, Integer> getMonsterHpMap() {
        if (this.monsterHpMapJson == null || this.monsterHpMapJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(this.monsterHpMapJson, new TypeReference<Map<String, Integer>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Transient
    public void setMonsterHpMap(Map<String, Integer> monsterHpMap) {
        if (monsterHpMap == null) {
            this.monsterHpMapJson = "{}";
            return;
        }
        try {
            this.monsterHpMapJson = objectMapper.writeValueAsString(monsterHpMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            this.monsterHpMapJson = "{}";
        }
    }

    // --- 其他遊戲邏輯方法 ---
    public boolean isAlive() { return this.hp > 0; }
    public void heal(int amount) { this.hp = Math.min(this.hp + amount, this.maxHp); }
    public void takeDamage(int dmg) { this.hp = Math.max(this.hp - dmg, 0); }
    public boolean hasSkill(String skillName) { return this.skillNames != null && this.skillNames.stream().anyMatch(s -> s.equalsIgnoreCase(skillName)); }
    public void recordUsedSkill(String skillName) { if (this.usedSkills != null && skillName != null) this.usedSkills.add(skillName.toLowerCase()); }
    public void addItem(String itemName, int quantity) { String key = itemName.toLowerCase(); this.inventory.put(key, getItemQuantity(key) + quantity); }
    public boolean removeItem(String itemName, int quantity) { String key = itemName.toLowerCase(); if (getItemQuantity(key) >= quantity) { this.inventory.put(key, getItemQuantity(key) - quantity); if (this.inventory.get(key) <= 0) { this.inventory.remove(key); } return true; } return false; }
    public int getItemQuantity(String itemName) { return this.inventory.getOrDefault(itemName.toLowerCase(), 0); }
    public void addDamage(int dmg) { this.totalDamage += dmg; }
    public void addKill() { this.killCount++; }
}
