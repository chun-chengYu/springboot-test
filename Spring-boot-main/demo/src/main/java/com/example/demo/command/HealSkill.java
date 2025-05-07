package com.example.demo.command; // 建議的包名

import com.example.demo.model.Player;
import com.example.demo.model.Monster;
import com.example.demo.model.Skill;
import java.util.ArrayList; // 引入 ArrayList
import java.util.List;    // 引入 List

public class HealSkill implements Skill { // 假設類名是 HealSkill

    private static final String SKILL_NAME = "heal";
    private static final int HEAL_AMOUNT = 25;

    @Override
    public String getName() {
        return SKILL_NAME;
    }

    @Override
    public List<String> use(Player player, Monster target) { // 返回類型現在是 List<String>
        List<String> messages = new ArrayList<>();

        if (player.getHp() >= player.getMaxHp()) {
            messages.add(player.getName() + " 的生命值已滿，無需治療。");
            // 技能未使用或無效，可以選擇返回只包含這條訊息的列表，或者一個空列表如果不想顯示
            return messages;
        }

        int actualHeal = Math.min(HEAL_AMOUNT, player.getMaxHp() - player.getHp());
        player.heal(actualHeal); // 假設 Player 類別有 public void heal(int amount) 方法

        messages.add(player.getName() + " 使用了 [" + SKILL_NAME + "] 技能，回復了 " + actualHeal + " 點生命值！");
        messages.add(player.getName() + " 目前 HP: " + player.getHp() + "/" + player.getMaxHp());

        // 'target' (Monster) 在這個自愈技能中未使用。
        return messages; // 返回包含執行訊息的列表
    }
}
