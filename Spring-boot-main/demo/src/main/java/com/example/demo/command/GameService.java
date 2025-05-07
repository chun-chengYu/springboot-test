package com.example.demo.command; // 假設的包名，請根據您的專案調整


import com.example.demo.model.GameContext;
import org.springframework.beans.factory.annotation.Autowired; // 如果使用 Spring DI
import org.springframework.stereotype.Service; // 如果使用 Spring DI

import java.util.List;
import java.util.ArrayList; // 用於 List.of() 的替代，或直接返回 context.getMessages()

@Service // 假設這是一個 Spring Service
public class GameService {

    private final CommandParser commandParser;
    private final GameContext gameContext; // GameService 需要能夠訪問 GameContext

    @Autowired // 使用建構子注入
    public GameService(CommandParser commandParser, GameContext gameContext) {
        this.commandParser = commandParser;
        this.gameContext = gameContext;
    }

    public List<String> processCommand(String input) {
        // 在處理每個指令前，清除 GameContext 中上一條指令留下的訊息
        gameContext.clearMessages();

        Command command = commandParser.parse(input);

        if (command == null) {
            // 如果指令無效，可以選擇直接返回錯誤訊息，或者通過 GameContext 添加
            // return List.of("無效的指令。"); // Java 9+
            // 或者
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("無效的指令。");
            return errorMessages;
        }

        // 執行指令。指令的 execute 方法是 void，它會直接修改 gameContext，
        // 包括通過 gameContext.addMessage() 添加訊息。
        if (gameContext.getPlayer() != null && gameContext.getPlayer().isAlive() && !gameContext.isGameOver()) {
            command.execute(gameContext.getPlayer(), gameContext);
        } else if (gameContext.getPlayer() == null) {
            gameContext.addMessage("錯誤：玩家未初始化！");
        } else if (gameContext.isGameOver()) {
             gameContext.addMessage("遊戲已結束。");
        } else if (!gameContext.getPlayer().isAlive()) {
            gameContext.addMessage(gameContext.getPlayer().getName() + " 已被擊敗！遊戲結束。");
            if (!gameContext.isGameOver()) { // 確保在玩家死亡時也設置遊戲結束
                gameContext.setGameOver(true);
            }
        }


        // 從 GameContext 中獲取由指令執行產生的所有訊息
        return gameContext.getMessages();
    }
}
