package com.example.demo.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LookResult implements CommandResult {
    private String roomName;
    private String description;
    private String monster;
    private boolean hasPotion;
    private Set<String> exits;
    
     @Override
    public List<String> getMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("你目前在：" + roomName);
        messages.add(description);
        
        if (monster != null && !monster.isEmpty()) {
            messages.add("你看到：" + monster);
        }
        
        if (hasPotion) {
            messages.add("有一瓶發光的治療藥水躺在地上。");
        }
        
        if (exits != null && !exits.isEmpty()) {
            messages.add("可用方向：" + String.join(", ", exits));
        } else {
            messages.add("可用方向：無");
        }
        
        return messages;
    }
    // 以下為 getters 和 setters
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        this.monster = monster;
    }

    public boolean isHasPotion() {
        return hasPotion;
    }

    public void setHasPotion(boolean hasPotion) {
        this.hasPotion = hasPotion;
    }

    public Set<String> getExits() {
        return exits;
    }

    public void setExits(Set<String> exits) {
        this.exits = exits;
    }
}
