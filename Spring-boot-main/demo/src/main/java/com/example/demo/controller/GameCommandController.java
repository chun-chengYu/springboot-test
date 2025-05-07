package com.example.demo.controller;

import com.example.demo.engine.GameEngine;
import com.example.demo.model.GameContext;
import com.example.demo.model.Player;
import com.example.demo.model.Room;
import com.example.demo.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/game")
public class GameCommandController {

    private static final Logger logger = LoggerFactory.getLogger(GameCommandController.class);

    private final GameEngine gameEngine;
    private final GameContext gameContext;
    private final PlayerService playerService;

    @Autowired
    public GameCommandController(GameEngine gameEngine, GameContext gameContext, PlayerService playerService) {
        this.gameEngine = gameEngine;
        this.gameContext = gameContext;
        this.playerService = playerService;
    }

    public static class CommandRequest {
        private String input;
        private Long playerId;

        public String getInput() { return input; }
        public void setInput(String input) { this.input = input; }
        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
    }

    @PostMapping("/command")
    public ResponseEntity<Map<String, Object>> handleCommand(@RequestBody CommandRequest request) {
        String input = request.getInput();
        Long playerId = request.getPlayerId();
        logger.info("接收到指令請求: '{}', 玩家ID: {}", input, playerId);

        if (playerId == null) {
            logger.warn("指令請求中缺少 playerId。");
            return ResponseEntity.badRequest().body(Map.of("messages", Arrays.asList("錯誤：請求無效，缺少玩家識別。"), "player", null, "gameState", gameEngine.getInitialGameStateForResponse()));
        }

        if (gameContext.getPlayer() == null || !playerId.equals(gameContext.getPlayer().getId())) {
            logger.info("GameContext 中的玩家與請求的 playerId {} 不符或為空。嘗試加載玩家...", playerId);
            Player playerFromDb = playerService.loadPlayerById(playerId);
            if (playerFromDb != null) {
                gameContext.setPlayer(playerFromDb);
                // vvVvv 修正點：將 findRoomById 修改為 getRoomById vVvvV
                Room currentRoom = gameContext.getRoomById(playerFromDb.getCurrentRoomId());
                // ^^^^ 修正點 ^^^^
                gameContext.setCurrentRoom(currentRoom);
                gameContext.clearMessages();
                gameContext.setGameOver(false);
                logger.info("已將玩家 {} (ID:{}) 加載到 GameContext。", playerFromDb.getName(), playerFromDb.getId());
            } else {
                logger.error("無法為指令處理加載玩家ID: {}。", playerId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(Map.of("messages", Arrays.asList("錯誤：無法驗證玩家身份或玩家不存在。"), "player", null, "gameState", gameEngine.getInitialGameStateForResponse()));
            }
        }

        if (gameContext.getPlayer() == null) {
            logger.error("指令處理前，GameContext 中的玩家仍然為空，即使嘗試加載 playerId: {}。", playerId);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(Map.of("messages", Arrays.asList("錯誤：玩家狀態未能正確初始化。"), "player", null, "gameState", gameEngine.getInitialGameStateForResponse()));
        }


        if (input == null || input.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("gameState", gameEngine.getInitialGameStateForResponse());
            errorResponse.put("messages", Arrays.asList("指令不能為空"));
            logger.info("收到空指令，返回預設狀態。");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> response = gameEngine.processCommand(input.trim());
        logger.info("指令 '{}' 處理完成，返回響應。", input);
        return ResponseEntity.ok(response);
    }
}
