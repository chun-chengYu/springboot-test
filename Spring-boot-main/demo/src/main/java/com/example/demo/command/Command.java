package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Player;

/**
 * 命令模式中的命令介面。
 * 所有具體的命令類別都應實現此介面。
 */
public interface Command {
    /**
     * 執行命令的邏輯。
     * @param player 執行命令的玩家。
     * @param context 當前的遊戲上下文。
     */
    void execute(Player player, GameContext context);
}
