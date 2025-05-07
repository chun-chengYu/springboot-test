package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Player;

public class ExitCommand implements Command {
    @Override
    public void execute(Player player, GameContext context) {
        context.addMessage("👋 感謝遊玩！再會，勇者！");
        context.setGameOver(true);
    }
}
