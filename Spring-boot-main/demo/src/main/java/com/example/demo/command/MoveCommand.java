package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Player;
import com.example.demo.model.Room;
import com.example.demo.model.Monster;

public class MoveCommand implements Command {
    private String direction;

    public MoveCommand(String direction) {
        this.direction = direction != null ? direction.toLowerCase().trim() : ""; // 添加 trim()
    }

    @Override
    public void execute(Player player, GameContext context) {
        if (direction.isEmpty()) {
            context.addMessage("請指定移動方向。 (例如: move north)");
            return;
        }

        Room currentRoom = context.getCurrentRoom();
        if (currentRoom == null) {
            context.addMessage("錯誤：當前房間未知，無法移動。請確保遊戲已正確初始化。");
            return;
        }

        String nextRoomId = currentRoom.getExit(direction);

        if (nextRoomId != null) {
            // 現在 GameContext 應該有 getRoomById 方法了
            Room nextRoom = context.getRoomById(nextRoomId);
            if (nextRoom != null) {
                context.setCurrentRoom(nextRoom);
                context.addMessage("你移動到了：" + nextRoom.getName());
                context.addMessage(nextRoom.getDescription()); // 顯示房間描述

                Monster monster = nextRoom.getMonster();
                if (monster != null && !monster.isDead()) {
                    context.addMessage("你看到：" + monster.getName() + "（HP: " + monster.getHp() + "）");
                } else if (monster != null && monster.isDead()) {
                     context.addMessage("你看到 " + monster.getName() + " (已被擊敗)。");
                } else {
                    context.addMessage("這裡看起來很安全，沒有怪物。");
                }

                // 顯示新房間的出口
                if (nextRoom.getExits() != null && !nextRoom.getExits().isEmpty()) {
                    context.addMessage("可用方向：" + String.join(", ", nextRoom.getExits().keySet()));
                } else {
                    context.addMessage("這裡似乎沒有其他出口。");
                }

                // 顯示新房間的物品 (示例)
                if (nextRoom.hasItem("healing_potion")) { // 假設 Room 有 hasItem 方法
                    context.addMessage("地上似乎有一瓶治療藥水。");
                }

            } else {
                context.addMessage("錯誤：目標房間 (" + nextRoomId + ") 在遊戲定義中未找到！請檢查 GameConfig。");
            }
        } else {
            context.addMessage("那個方向 (" + direction + ") 無法移動。");
        }
    }
}
