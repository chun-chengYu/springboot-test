package com.example.demo.model; // 確保包名與您的項目結構一致

import java.util.List; // <<< --- 新增或確保此 import 語句存在

public interface Skill {
    /**
     * 獲取技能的名稱。
     * @return 技能名稱字串。
     */
    String getName();

    /**
     * 執行技能的邏輯，並返回執行過程中產生的遊戲訊息列表。
     * @param player 使用技能的玩家。
     * @param target 技能的目標怪物 (對於某些技能可能未使用)。
     * @return 包含遊戲訊息的 List<String>。如果沒有訊息，可以返回空列表。
     */
    List<String> use(Player player, Monster target); // <<< --- 返回類型是 List<String>
}
