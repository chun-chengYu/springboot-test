package com.example.demo.engine;

import com.example.demo.command.Command;
import com.example.demo.command.CommandParser;
import com.example.demo.dto.GameStateDTO; // 假設您使用 GameStateDTO
import com.example.demo.model.GameContext;
import com.example.demo.model.Monster;
import com.example.demo.model.Player;
import com.example.demo.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class GameEngine {

    private final GameContext context;
    private final CommandParser commandParser;
    private boolean initialLookDone = false;

    @Autowired
    public GameEngine(GameContext context, CommandParser commandParser) {
        this.context = context;
        this.commandParser = commandParser;
        // 注意：如果 GameContext 是 @SessionScope，它在 GameEngine 構造時可能還沒有 Player
        // Player 的加載/創建應更新 GameContext
    }

    private void performInitialLook() {
        if (context.getPlayer() != null) {
            context.addMessage("👾 [ 遊戲開始！歡迎" + context.getPlayer().getName() + "勇者！ ]");
        }
        Room startRoom = context.getCurrentRoom();
        if (startRoom != null) {
            context.addMessage("你目前在：" + startRoom.getName());
            Monster monster = startRoom.getMonster();
            if (monster != null && !monster.isDead()) {
                context.addMessage("你看到：" + monster.getName() + "（HP: " + monster.getHp() + "）");
            }
             if (monster != null && monster.isDead()) {
                 context.addMessage("你看到 " + monster.getName() + " (已被擊敗)。");
            }
            if (!startRoom.getExits().isEmpty()) {
                 context.addMessage("可用方向：" + String.join(", ", startRoom.getExits().keySet()));
            }
            if (startRoom.hasItem("healing_potion")) { // 假設 Room 有 hasItem
                 context.addMessage("有一瓶發光的治療藥水躺在地上。");
            }
        }
        initialLookDone = true;
    }

    public Map<String, Object> processCommand(String input) {
        Map<String, Object> response = new HashMap<>();
        context.clearMessages(); // 清除上一條指令的訊息

        // 確保 GameContext 中有 Player，如果沒有，可能是用戶還未創建/加載角色
        if (context.getPlayer() == null) {
            // GameCommandController 在 input 為空時會調用 getInitialGameStateForResponse
            // 如果 input 不為空但 player 為 null，這裡應該提示用戶創建/加載角色
            context.addMessage("錯誤：玩家角色尚未創建或加載。請先創建角色或讀取進度。");
            response.put("player", null); // 明確表示沒有玩家
            response.put("gameState", getInitialGameStateForResponse()); // 返回一個空的或預設的 gameState
            response.put("messages", new ArrayList<>(context.getMessages()));
            return response;
        }

        if (!initialLookDone) { // 只有當玩家存在時才執行 initialLook
            performInitialLook();
        }

        if (context.isGameOver()) {
            context.addMessage("遊戲已結束。請重新開始。");
        } else if (!context.getPlayer().isAlive()) {
            if (!context.getMessages().contains(context.getPlayer().getName() + " 已被擊敗！遊戲結束。")) {
                 context.addMessage(context.getPlayer().getName() + " 已被擊敗！遊戲結束。");
            }
            context.setGameOver(true);
        } else {
            Command command = commandParser.parse(input);
            if (command != null) {
                command.execute(context.getPlayer(), context);
            } else if (input != null && !input.trim().isEmpty()){
                context.addMessage("無效的指令：" + input);
            }
        }

        // 構造響應
        Player currentPlayer = context.getPlayer();
        response.put("player", currentPlayer);

        GameStateDTO gameStateDTO = new GameStateDTO();
        if (context.getCurrentRoom() != null) {
            gameStateDTO.setCurrentRoom(context.getCurrentRoom().getId());
        } else if (currentPlayer != null && currentPlayer.getCurrentRoomId() != null) {
            gameStateDTO.setCurrentRoom(currentPlayer.getCurrentRoomId());
        } else {
            gameStateDTO.setCurrentRoom("forest_entrance"); // 預設
        }

        if (currentPlayer != null) {
            gameStateDTO.setMonsterHpMap(currentPlayer.getMonsterHpMap());
        } else {
            gameStateDTO.setMonsterHpMap(new HashMap<>()); // 空的
        }
        // 確保 gameOver 狀態被包含 (如果 GameStateDTO 有此欄位)
        // gameStateDTO.setGameOver(context.isGameOver());

        response.put("gameState", gameStateDTO); // 將 DTO 作為 gameState 返回
        response.put("messages", new ArrayList<>(context.getMessages()));

        return response;
    }

    /**
     * 獲取用於響應的初始或預設遊戲狀態。
     * 被 GameCommandController 在處理空指令或玩家未初始化時調用。
     * @return 一個代表遊戲狀態的 Map，其結構應與前端JS中的 gameState 物件相匹配。
     */
    public Map<String, Object> getInitialGameStateForResponse() {
        Map<String, Object> initialGameState = new HashMap<>();
        Player currentPlayerInContext = context.getPlayer(); // 可能為 null

        String currentRoomId = "forest_entrance"; // 預設初始房間ID
        if (context.getCurrentRoom() != null) {
            currentRoomId = context.getCurrentRoom().getId();
        } else if (currentPlayerInContext != null && currentPlayerInContext.getCurrentRoomId() != null) {
            currentRoomId = currentPlayerInContext.getCurrentRoomId();
        }
        initialGameState.put("currentRoom", currentRoomId);

        Map<String, Integer> monsterHp = new HashMap<>();
        if (currentPlayerInContext != null && currentPlayerInContext.getMonsterHpMap() != null && !currentPlayerInContext.getMonsterHpMap().isEmpty()) {
            monsterHp.putAll(currentPlayerInContext.getMonsterHpMap());
        } else {
            // 如果沒有玩家或玩家沒有怪物HP信息，提供一個預設的（例如初始房間的怪物）
            if ("forest_entrance".equals(currentRoomId)) {
                monsterHp.put("forest_entrance", 30); // 假設森林入口有哥布林，HP 30
            }
            // 您可能需要根據遊戲的房間定義來獲取更準確的預設怪物HP
        }
        initialGameState.put("monsterHpMap", monsterHp);
        initialGameState.put("gameOver", context.isGameOver()); // 包含 gameOver 狀態

        // 注意：這裡返回的是 gameState Map 本身，
        // GameCommandController 會將這個 Map 放在其響應的 "gameState" 鍵下。
        return initialGameState;
    }
}
