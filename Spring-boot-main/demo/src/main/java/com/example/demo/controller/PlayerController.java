package com.example.demo.controller;

import com.example.demo.dto.CreatePlayerRequestDTO;
import com.example.demo.dto.GameStateDTO;
import com.example.demo.dto.PlayerSaveRequestDTO;
import com.example.demo.model.GameContext;
import com.example.demo.model.Player;
import com.example.demo.model.Room;
import com.example.demo.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections; // 引入 Collections
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerService playerService;
    private final GameContext gameContext; // 注入會話範圍的 GameContext
    private final List<Room> allGameRooms; // 從 GameConfig 注入所有房間定義

    @Autowired
    public PlayerController(PlayerService playerService, GameContext gameContext, List<Room> allGameRooms) {
        this.playerService = playerService;
        this.gameContext = gameContext;
        // 確保 allGameRooms 被正確注入，如果為 null，可能 GameConfig 有問題
        this.allGameRooms = (allGameRooms != null) ? allGameRooms : Collections.emptyList();
        if (this.allGameRooms.isEmpty()) {
            logger.warn("PlayerController: allGameRooms 列表為空，請檢查 GameConfig 配置。");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPlayer(@RequestBody CreatePlayerRequestDTO createRequest) {
        try {
            logger.info("接收到創建角色請求，名稱: {}", createRequest.getName());
            if (createRequest.getName() == null || createRequest.getName().trim().isEmpty()) {
                logger.warn("創建角色失敗：玩家名稱不能為空。");
                return ResponseEntity.badRequest().body(Map.of("messages", Arrays.asList("玩家名稱不能為空")));
            }

            String initialRoomId = "forest_entrance"; // 預設初始房間ID
            // 確保 playerService.createNewPlayer 內部有充分的錯誤處理和日誌
            Player savedPlayer = playerService.createNewPlayer(createRequest.getName(), initialRoomId);
            if (savedPlayer == null || savedPlayer.getId() == null) {
                logger.error("創建角色後，playerService.createNewPlayer 返回了 null 或無效的 Player 物件。");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(Map.of("messages", Arrays.asList("角色創建過程中發生內部錯誤。")));
            }
            logger.info("角色 {} (ID:{}) 創建並保存成功。", savedPlayer.getName(), savedPlayer.getId());

            Room startRoom = findRoomById(initialRoomId);
            if (startRoom == null) {
                logger.warn("找不到初始房間 ID: {}。將嘗試使用第一個可用房間。", initialRoomId);
                if (!this.allGameRooms.isEmpty()) {
                    startRoom = this.allGameRooms.get(0);
                } else {
                    logger.error("創建角色失敗：沒有可用的房間來設置初始位置。請檢查 GameConfig。");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                         .body(Map.of("messages", Arrays.asList("遊戲配置錯誤：無法設置初始房間。")));
                }
            }
            logger.info("初始房間設定為: {}", startRoom.getName());


            // 更新會話 GameContext
            gameContext.setPlayer(savedPlayer);
            gameContext.setCurrentRoom(startRoom);
            gameContext.clearMessages();
            gameContext.setGameOver(false);
            logger.info("GameContext 已為玩家 {} 初始化。", savedPlayer.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("player", savedPlayer); // 返回完整的 Player 物件
            GameStateDTO initialGameState = createGameStateDTOFromContext(gameContext);
            response.put("gameState", initialGameState);
            response.put("messages", Arrays.asList("角色創建成功！"));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // 記錄更詳細的異常信息
            logger.error("創建角色時發生未預期錯誤: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("messages", Arrays.asList("創建角色失敗: " + e.getMessage())));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody PlayerSaveRequestDTO saveRequest) {
        try {
            logger.info("接收到保存玩家請求，ID: {}", saveRequest.getId());
            if (saveRequest.getId() == null) {
                logger.warn("保存請求缺少玩家ID。");
                return ResponseEntity.badRequest().body(Map.of("messages", Arrays.asList("保存請求缺少玩家ID")));
            }
            Player playerToSave = playerService.loadPlayerById(saveRequest.getId());
            if (playerToSave == null) {
                 logger.warn("找不到ID為 {} 的玩家進行更新。", saveRequest.getId());
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("messages", Arrays.asList("找不到ID為 " + saveRequest.getId() + " 的玩家進行更新")));
            }

            // 更新 Player 實體
            playerToSave.setName(saveRequest.getName());
            playerToSave.setHp(saveRequest.getHp());
            playerToSave.setMaxHp(saveRequest.getMaxHp());
            playerToSave.setAttack(saveRequest.getAttack());
            playerToSave.setTotalDamage(saveRequest.getTotalDamage());
            playerToSave.setKillCount(saveRequest.getKillCount());
            playerToSave.setSkillNames(saveRequest.getSkillNames());
            playerToSave.setUsedSkills(saveRequest.getUsedSkills());
            playerToSave.setInventory(saveRequest.getInventory());

            if (saveRequest.getGameState() != null) {
                playerToSave.setCurrentRoomId(saveRequest.getGameState().getCurrentRoom());
                playerToSave.setMonsterHpMap(saveRequest.getGameState().getMonsterHpMap());
                // 如果 GameStateDTO 包含 gameOver 狀態，也應該在這裡更新 Player 實體（如果 Player 實體有此欄位）
                // playerToSave.setGameOver(saveRequest.getGameState().isGameOver());
            }

            Player savedPlayer = playerService.savePlayer(playerToSave);
            logger.info("玩家 {} (ID:{}) 資料保存成功。", savedPlayer.getName(), savedPlayer.getId());

            // 如果當前會話的 GameContext 正在操作這個玩家，則更新它
            if (gameContext.getPlayer() != null && gameContext.getPlayer().getId().equals(savedPlayer.getId())) {
                gameContext.setPlayer(savedPlayer); // 更新 GameContext 中的玩家實例
                Room currentRoomInContext = findRoomById(savedPlayer.getCurrentRoomId());
                gameContext.setCurrentRoom(currentRoomInContext); // 更新 GameContext 中的當前房間
                logger.info("GameContext 中的玩家 {} 已更新。", savedPlayer.getName());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("messages", Arrays.asList("玩家資料保存成功"));
            response.put("player", savedPlayer);
            response.put("gameState", createGameStateDTOFromPlayer(savedPlayer));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("保存玩家時發生未預期錯誤: ", e);
            return createErrorResponse("保存失敗: " + e.getMessage());
        }
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<?> load(@PathVariable Long id) {
        try {
            logger.info("接收到讀取玩家請求，ID: {}", id);
            Player loadedPlayer = playerService.loadPlayerById(id);
            if (loadedPlayer == null) {
                logger.warn("找不到ID為 {} 的玩家資料。", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("messages", Arrays.asList("找不到ID為 " + id + " 的玩家資料")));
            }
            logger.info("成功讀取玩家 {} (ID:{})。", loadedPlayer.getName(), loadedPlayer.getId());

            Room currentRoom = findRoomById(loadedPlayer.getCurrentRoomId());
             if (currentRoom == null) {
                logger.warn("找不到玩家 {} (ID:{}) 的當前房間ID: {}。將嘗試使用第一個可用房間。", loadedPlayer.getName(), id, loadedPlayer.getCurrentRoomId());
                if (!this.allGameRooms.isEmpty()) {
                    currentRoom = this.allGameRooms.get(0);
                } else {
                    logger.error("讀取玩家失敗：沒有可用的房間來設置當前位置。請檢查 GameConfig。");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                         .body(Map.of("messages", Arrays.asList("遊戲配置錯誤：無法設置當前房間。")));
                }
            }
            logger.info("玩家 {} (ID:{}) 的當前房間設定為: {}", loadedPlayer.getName(), id, currentRoom.getName());


            // 更新會話 GameContext
            gameContext.setPlayer(loadedPlayer);
            gameContext.setCurrentRoom(currentRoom);
            gameContext.clearMessages();
            gameContext.setGameOver(false); // 讀取時重置遊戲結束狀態
            logger.info("GameContext 已為讀取的玩家 {} 初始化。", loadedPlayer.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("messages", Arrays.asList("玩家資料讀取成功"));
            response.put("player", loadedPlayer);
            response.put("gameState", createGameStateDTOFromPlayer(loadedPlayer));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("讀取玩家時發生未預期錯誤: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("messages", Arrays.asList("讀取失敗: " + e.getMessage())));
        }
    }

    private ResponseEntity<?> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("messages", Arrays.asList(errorMessage));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 從 Player 實體創建 GameStateDTO，用於返回給前端
    private GameStateDTO createGameStateDTOFromPlayer(Player player) {
        GameStateDTO gameStateDTO = new GameStateDTO();
        if (player != null) {
            gameStateDTO.setCurrentRoom(player.getCurrentRoomId());
            gameStateDTO.setMonsterHpMap(player.getMonsterHpMap()); // Player 應有 getMonsterHpMap()
            // 假設遊戲開始或加載時，gameOver 狀態為 false，除非 Player 實體本身存儲了 gameOver 狀態
            // gameStateDTO.setGameOver(player.isGameOver()); // 如果 Player 實體有 isGameOver 欄位
            gameStateDTO.setGameOver(false); // 讀取/創建後通常遊戲是進行中的
        }
        return gameStateDTO;
    }

    // 從 GameContext 創建 GameStateDTO，用於 /create 端點返回初始狀態
    private GameStateDTO createGameStateDTOFromContext(GameContext context) {
        GameStateDTO gameStateDTO = new GameStateDTO();
        if (context != null) {
            if (context.getCurrentRoom() != null) {
                gameStateDTO.setCurrentRoom(context.getCurrentRoom().getId());
            } else if (context.getPlayer() != null && context.getPlayer().getCurrentRoomId() != null){
                // 備用，如果 gameContext.currentRoom 未被直接設置
                gameStateDTO.setCurrentRoom(context.getPlayer().getCurrentRoomId());
            }

            if (context.getPlayer() != null) {
                gameStateDTO.setMonsterHpMap(context.getPlayer().getMonsterHpMap());
            }
            gameStateDTO.setGameOver(context.isGameOver());
        }
        return gameStateDTO;
    }

     // 輔助方法，在本 Controller 內部根據 ID 查找房間
     private Room findRoomById(String roomId) {
        if (roomId == null || this.allGameRooms == null || this.allGameRooms.isEmpty()) {
            logger.warn("findRoomById: roomId 為空或 allGameRooms 列表為空/null。");
            return null;
        }
        return this.allGameRooms.stream()
                              .filter(r -> r != null && roomId.equalsIgnoreCase(r.getId())) // 增加對 r != null 的檢查
                              .findFirst()
                              .orElse(null);
    }
}
