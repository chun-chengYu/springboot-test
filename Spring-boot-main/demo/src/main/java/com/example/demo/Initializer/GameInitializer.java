package com.example.demo.Initializer;

import com.example.demo.model.*;
import com.example.demo.model.FireballSkill; // 示例路徑，請根據您的項目結構調整
// import com.example.demo.model.skill.HealSkill; // 如果有 HealSkill

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList; // <<< --- 新增或確保此 import 語句存在
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameInitializer {

    private final SkillRegistry skillRegistry;
    private final List<Room> allGameRooms;

    @Autowired
    public GameInitializer(SkillRegistry skillRegistry, List<Room> allGameRooms) {
        this.skillRegistry = skillRegistry;
        this.allGameRooms = new ArrayList<>(allGameRooms); // 這裡使用 ArrayList 是正確的
    }

    public GameContext initGameForNewPlayer(String playerName, String initialRoomId) {
        // 創建房間實例 (可以從 allGameRooms 獲取)
        Room forest = findOrInitializeRoom("forest_entrance", "森林入口", "一個古老森林的入口，充滿了神秘的氣息。", new Monster("哥布林", 30, 8), true);
        Room temple = findOrInitializeRoom("temple_hall", "廢棄神殿大廳", "宏偉但已殘破的大廳，光線從頂部的裂縫灑下。", new Monster("亡靈戰士", 50, 12), false);

        Room actualForest = findRoomByIdInternal(forest.getId());
        Room actualTemple = findRoomByIdInternal(temple.getId());

        if (actualForest != null && actualTemple != null) {
            actualForest.setExit("north", actualTemple);
            actualTemple.setExit("south", actualForest);
        }

        List<Skill> skillObjects = Collections.singletonList(new FireballSkill());
        List<String> defaultSkillNames = skillObjects.stream()
                                           .map(Skill::getName)
                                           .collect(Collectors.toList());

        Player player = new Player();
        player.initialize(playerName, 100, 15, defaultSkillNames, initialRoomId);

        Room startRoom = findRoomByIdInternal(initialRoomId);
        if (startRoom == null) {
            System.err.println("錯誤：在 GameInitializer 中找不到 ID 為 " + initialRoomId + " 的起始房間！將使用第一個房間。");
            if (!this.allGameRooms.isEmpty()) {
                startRoom = this.allGameRooms.get(0);
            } else {
                throw new IllegalStateException("遊戲初始化錯誤：沒有定義任何房間！");
            }
        }

        GameContext context = new GameContext(); // 假設 GameContext 有無參構造
        context.setPlayer(player);
        context.setAllRooms(new ArrayList<>(this.allGameRooms)); // 這裡使用 ArrayList 是正確的
        context.setCurrentRoom(startRoom);
        context.setSkillRegistry(this.skillRegistry);

        return context;
    }

    public GameContext getDefaultInitialContext() {
        String defaultPlayerName = "預設勇者";
        String defaultInitialRoomId = "forest_entrance";
        return initGameForNewPlayer(defaultPlayerName, defaultInitialRoomId);
    }

    private Room findOrInitializeRoom(String id, String name, String description, Monster monster, boolean hasPotion) {
        Room existingRoom = findRoomByIdInternal(id);
        if (existingRoom != null) {
            return existingRoom;
        }
        return new Room(id, name, description, monster, hasPotion);
    }

    private Room findRoomByIdInternal(String roomId) {
        if (roomId == null || this.allGameRooms == null || this.allGameRooms.isEmpty()) {
            return null;
        }
        return this.allGameRooms.stream()
                       .filter(r -> r != null && roomId.equalsIgnoreCase(r.getId()))
                       .findFirst()
                       .orElse(null);
    }
}
