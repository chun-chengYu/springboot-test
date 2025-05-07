package com.example.demo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@SessionScope
public class GameContext {
    private Player player;
    private Room currentRoom;
    private List<Room> allRooms; // 由 GameConfig 注入
    private List<String> messages;
    private boolean gameOver = false;
    private SkillRegistry skillRegistry; // 由 GameConfig 注入

    // Spring 用於依賴注入的建構子
    @Autowired
    public GameContext(List<Room> allGameRooms, SkillRegistry skillRegistry) {
        this.allRooms = new ArrayList<>(allGameRooms); // 初始化所有房間
        this.skillRegistry = skillRegistry;         // 初始化技能註冊表
        this.messages = new ArrayList<>();
        System.out.println("GameContext (會話範圍) 已通過 Autowired 建構子使用 allGameRooms 和 skillRegistry 初始化。");
    }

    // 無參數建構子 (如果其他地方如 GameInitializer 直接 new GameContext()，則需要)
    // 但如果 GameContext 完全由 Spring 管理並通過 @Autowired 建構子注入，則此無參構造可能不是必需的
    // 或者，如果 GameInitializer 也被 Spring 管理並注入 List<Room> 和 SkillRegistry，
    // 它可以創建 GameContext 實例並手動設置這些依賴。
    public GameContext() {
        this.allRooms = new ArrayList<>();
        this.messages = new ArrayList<>();
        System.out.println("GameContext (會話範圍) 的無參數建構子被調用。allRooms 和 skillRegistry 需要後續設置。");
    }


    public Player getPlayer() { return player; }
    public void setPlayer(Player player) {
        this.player = player;
        if (this.player != null && this.player.getCurrentRoomId() != null && this.allRooms != null && !this.allRooms.isEmpty()) {
            this.currentRoom = findRoomByIdInternal(this.player.getCurrentRoomId()); // 使用內部方法
        } else if (this.player == null) {
             this.currentRoom = null;
        }
    }

    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room room) { this.currentRoom = room; }

    public List<Room> getAllRooms() {
        return (allRooms != null) ? Collections.unmodifiableList(allRooms) : Collections.emptyList();
    }
    // 如果 allRooms 通過建構子注入，則此 setter 可能不是必需的，除非需要動態更改
    public void setAllRooms(List<Room> allRooms) {
        this.allRooms = (allRooms != null) ? new ArrayList<>(allRooms) : new ArrayList<>();
    }

    // vvVvv 這是 MoveCommand 需要的方法 vVvvV
    /**
     * 根據房間 ID 從所有房間列表中查找並返回房間對象。
     * @param roomId 要查找的房間的 ID。
     * @return 如果找到則返回 Room 對象，否則返回 null。
     */
    public Room getRoomById(String roomId) {
        if (roomId == null || this.allRooms == null || this.allRooms.isEmpty()) {
            return null;
        }
        for (Room room : this.allRooms) {
            if (room != null && roomId.equalsIgnoreCase(room.getId())) {
                return room;
            }
        }
        return null; // 沒有找到匹配的房間
    }
    // ^^^^ 確保此方法存在且為 public ^^^^

    // 內部使用的 findRoomById，可以保持 private 或改名以區分
    private Room findRoomByIdInternal(String roomId) {
        return getRoomById(roomId); // 可以直接調用公共的 getRoomById
    }


    public SkillRegistry getSkillRegistry() { return skillRegistry; }
    // 如果 skillRegistry 通過建構子注入，則此 setter 可能不是必需的
    public void setSkillRegistry(SkillRegistry skillRegistry) { this.skillRegistry = skillRegistry; }

    public List<String> getMessages() { return (messages != null) ? new ArrayList<>(this.messages) : new ArrayList<>(); }
    public void addMessage(String message) {
        if (this.messages == null) this.messages = new ArrayList<>();
        this.messages.add(message);
    }
    public void clearMessages() {
        if (this.messages != null) this.messages.clear();
        else this.messages = new ArrayList<>();
    }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public Map<String, Object> getGameStateForResponse() {
        Map<String, Object> gameStateMap = new HashMap<>();
        if (currentRoom != null) {
            gameStateMap.put("currentRoom", currentRoom.getId());
        } else if (player != null && player.getCurrentRoomId() != null) {
             gameStateMap.put("currentRoom", player.getCurrentRoomId());
        } else {
            gameStateMap.put("currentRoom", null);
        }

        if (player != null) {
            gameStateMap.put("monsterHpMap", player.getMonsterHpMap());
        } else {
            gameStateMap.put("monsterHpMap", new HashMap<>());
        }
        gameStateMap.put("gameOver", this.gameOver);
        return gameStateMap;
    }
}
