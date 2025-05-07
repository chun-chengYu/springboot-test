package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Player;

// 修正點：確保 HealCommand 實現了 Command 介面
public class HealCommand implements Command {

    private static final int DEFAULT_HEAL_AMOUNT = 20; // 預設治療量

    public HealCommand() {
        // 無參構造函數
    }

    @Override
    public void execute(Player player, GameContext context) {
        if (player == null) {
            context.addMessage("錯誤：玩家不存在，無法執行治療。");
            return;
        }

        if (player.getHp() >= player.getMaxHp()) {
            context.addMessage(player.getName() + " 的生命值已滿，無需治療。");
            return;
        }

        int actualHeal = Math.min(DEFAULT_HEAL_AMOUNT, player.getMaxHp() - player.getHp());
        player.heal(actualHeal); // 假設 Player 類別有 public void heal(int amount) 方法

        context.addMessage(player.getName() + " 使用了治療指令，回復了 " + actualHeal + " 點 HP！");
        context.addMessage(player.getName() + " 目前 HP：" + player.getHp() + "/" + player.getMaxHp());
    }
}
