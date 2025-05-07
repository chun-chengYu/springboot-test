package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Player;
import com.example.demo.model.Room;
import com.example.demo.model.Monster;

// import java.util.stream.Collectors; // <--- 刪除這一行

public class LookCommand implements Command {
    @Override
    public void execute(Player player, GameContext context) {
        Room room = context.getCurrentRoom();
        if (room == null) {
            context.addMessage("錯誤：你不在任何房間！");
            return;
        }

        context.addMessage("【房間】：" + room.getName());
        context.addMessage("【描述】：" + room.getDescription());

        Monster monster = room.getMonster();
        if (monster != null) {
            if (!monster.isDead()) {
                context.addMessage("【怪物】：" + monster.getName() + "（HP: " + monster.getHp() + "）");
            } else {
                context.addMessage("【怪物】：" + monster.getName() + "（已被擊敗）");
            }
        }

        if (!room.getItems().isEmpty()) {
            // String.join 不需要 Collectors
            context.addMessage("【道具】：" + String.join(", ", room.getItems()));
        }

        if (!room.getExits().isEmpty()) {
            // room.getExits().keySet() 返回一個 Set<String>，String.join 可以直接處理它
            context.addMessage("【出口】：" + String.join(", ", room.getExits().keySet()));
        } else {
            context.addMessage("【出口】：無");
        }
    }
}
