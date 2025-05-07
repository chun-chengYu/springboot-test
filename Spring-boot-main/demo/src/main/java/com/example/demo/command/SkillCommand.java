package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Monster;
import com.example.demo.model.Player;
import com.example.demo.model.Room;
import com.example.demo.model.SkillRegistry;
import com.example.demo.model.Skill; // Skill 介面現在的 use 方法返回 List<String>

import java.util.List; // <<< --- 確保引入 List

public class SkillCommand implements Command {
    private final String skillNameInput;
    private final SkillRegistry skillRegistry;

    public SkillCommand(String skillNameInput, SkillRegistry skillRegistry) {
        this.skillNameInput = skillNameInput != null ? skillNameInput.toLowerCase() : "";
        this.skillRegistry = skillRegistry;
    }

    @Override
    public void execute(Player player, GameContext context) {
        if (this.skillRegistry == null) {
            context.addMessage("錯誤：SkillRegistry 未初始化！");
            return;
        }
        if (this.skillNameInput.isEmpty()) {
            context.addMessage("請指定要使用的技能。 (例如: skill fireball)");
            return;
        }

        if (!player.hasSkill(this.skillNameInput)) { // 假設 Player 有 hasSkill(String) 方法
            context.addMessage("你尚未習得技能：" + this.skillNameInput);
            return;
        }

        Skill skillToUse = skillRegistry.getSkill(this.skillNameInput);

        if (skillToUse == null) {
            context.addMessage("錯誤：技能 '" + this.skillNameInput + "' 未在技能註冊表中定義或無效。");
            return;
        }

        Room currentRoom = context.getCurrentRoom();
        if (currentRoom == null) {
            context.addMessage("錯誤：玩家不在任何房間！");
            return;
        }

        Monster targetMonster = currentRoom.getMonster(); // 獲取當前房間的怪物作為目標

        // vvVvv 修正點：skillToUse.use 返回 List<String> vVvvV
        List<String> skillMessages = skillToUse.use(player, targetMonster);
        // ^^^^ 此處不再報錯，因為 skillMessages 現在是 List<String> ^^^^

        // 將技能產生的訊息添加到 GameContext
        if (skillMessages != null && !skillMessages.isEmpty()) {
            for (String msg : skillMessages) {
                context.addMessage(msg);
            }
            // 可以在這裡判斷 skillMessages 的內容來決定是否額外添加 "成功使用" 或 "使用失敗" 的訊息
            // 例如，如果 HealSkill 在HP滿時返回的 List<String> 包含特定標識，可以在這裡處理
        } else {
            // 如果技能返回 null 或空列表，可能表示使用失敗或無訊息產生
            context.addMessage(player.getName() + " 使用技能 " + skillToUse.getName() + "，但沒有產生額外訊息。");
        }


        // 檢查玩家/怪物狀態，遊戲結束等邏輯
        if (!player.isAlive() && !context.isGameOver()) {
            context.addMessage(player.getName() + " 已被擊敗！遊戲結束。");
            context.setGameOver(true);
        } else if (targetMonster != null && targetMonster.isDead() && player.isAlive()) {
            // 確保這條訊息只在怪物剛剛被此技能擊敗時顯示，避免重複
            // Skill 的 use 方法返回的 messages 中應已包含擊敗訊息
            // context.addMessage("你擊敗了 " + targetMonster.getName() + "！"); // 此訊息可能已在 skillMessages 中
            player.addKill(); // 假設 Player 有 addKill()
        }

        // 通用的狀態更新訊息
        // 這些訊息應在技能核心邏輯執行完畢後添加，以反映最新狀態
        Monster monsterAfterSkill = currentRoom.getMonster(); // 重新獲取，因為狀態可能已改變
        // 只有在有怪物目標（即使怪物剛被擊敗）或者某些無目標技能（如治療）後才顯示特定狀態
        if (targetMonster != null || "heal".equalsIgnoreCase(skillToUse.getName())) { // 示例：治療技能也更新狀態
            context.addMessage("=== 狀態更新 ===");
            context.addMessage("你的 HP：" + player.getHp() + "/" + player.getMaxHp());
            if (targetMonster != null) { // 如果有目標怪物
                if (monsterAfterSkill.isDead()) { // monsterAfterSkill 是更新後的狀態
                    context.addMessage(monsterAfterSkill.getName() + " HP：0 (已擊敗)");
                } else {
                     context.addMessage(monsterAfterSkill.getName() + " HP：" + monsterAfterSkill.getHp() + "/" + monsterAfterSkill.getMaxHp());
                }
                context.addMessage("總傷害：" + player.getTotalDamage() + "，擊殺數：" + player.getKillCount());
            }
        }
    }
}
