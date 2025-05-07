package com.example.demo.model;

import java.util.*;

public class Room {
    private String id; // 房間的唯一標識
    private String name;
    private String description;
    private Monster monster;
    private Map<String, String> exits; // 方向 -> 目標房間 ID
    private List<String> items;

    // 修改建構子，移除 boolean，並允許稍後設置出口和物品
    public Room(String id, String name, String description, Monster monster) {
        this.id = id; // 需要一個唯一的 ID
        this.name = name;
        this.description = description;
        this.monster = monster;
        this.exits = new HashMap<>(); // 初始化為空 Map
        this.items = new ArrayList<>(); // 初始化為空 List
    }

    // 如果GameInitializer中傳入的boolean參數代表是否有藥水，可以這樣處理
    public Room(String id, String name, String description, Monster monster, boolean hasInitialPotion) {
        this(id, name, description, monster); // 調用上面的建構子
        if (hasInitialPotion) {
            this.items.add("healing_potion"); // 假設藥水名稱
        }
    }


    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }
    public Map<String, String> getExits() { return Collections.unmodifiableMap(exits); }

    // 新增 setExit 方法，存儲目標房間的 ID
    public void setExit(String direction, Room targetRoom) {
        if (direction != null && targetRoom != null && targetRoom.getId() != null) {
            this.exits.put(direction.toLowerCase(), targetRoom.getId());
        }
    }

    public String getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public List<String> getItems() { return Collections.unmodifiableList(items); }
    public boolean hasItem(String itemName) { return items.stream().anyMatch(item -> item.equalsIgnoreCase(itemName)); }
    public boolean removeItem(String itemName) { return items.removeIf(item -> item.equalsIgnoreCase(itemName)); }
    public void addItem(String itemName) { if (!items.contains(itemName.toLowerCase())) { items.add(itemName.toLowerCase()); } }
}
