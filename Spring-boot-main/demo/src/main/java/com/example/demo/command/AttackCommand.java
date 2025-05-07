package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Monster;
import com.example.demo.model.Player;
import com.example.demo.model.Room;

public class AttackCommand implements Command {
    @Override
    public void execute(Player player, GameContext context) {
        Room currentRoom = context.getCurrentRoom();
        if (currentRoom == null) {
            context.addMessage("錯誤：玩家不在任何房間！");
            return;
        }
        Monster monster = currentRoom.getMonster();

        if (monster != null && !monster.isDead()) {
            int playerDamage = player.getAttack();
            context.addMessage("你對 " + monster.getName() + " 造成了 " + playerDamage + " 傷害！");
            monster.takeDamage(playerDamage);
            player.addDamage(playerDamage);

            if (monster.isDead()) {
                context.addMessage(monster.getName() + " 被擊倒！"); // 使用範本中的詞語
                player.addKill();
            } else {
                int monsterDamage = monster.getAttackPower();
                context.addMessage(monster.getName() + " 反擊你，造成 " + monsterDamage + " 傷害！");
                player.takeDamage(monsterDamage);
                if (!player.isAlive()) {
                    context.addMessage(player.getName() + " 已被擊敗！遊戲結束。");
                    context.setGameOver(true);
                }
            }
            // 狀態更新信息
            context.addMessage("=== 狀態更新 ===");
            context.addMessage("你的 HP：" + player.getHp());
            if (!monster.isDead() || monster.getHp() == 0) {
                 context.addMessage(monster.getName() + " HP：" + monster.getHp());
            }
            context.addMessage("總傷害：" + player.getTotalDamage() + "，擊殺數：" + player.getKillCount());

        } else if (monster != null && monster.isDead()) {
            context.addMessage(monster.getName() + " 已經被擊敗了。");
        } else {
            context.addMessage("這裡沒有可以攻擊的目標。");
        }
    }
}
