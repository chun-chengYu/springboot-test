package com.example.demo.controller;

import com.example.demo.model.Player;
import com.example.demo.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@Controller
public class PlayerWebController {
    private static final Logger logger = LoggerFactory.getLogger(PlayerWebController.class);
    
    private final PlayerService playerService;

    public PlayerWebController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public String showPlayers(Model model) {
        try {
            List<Player> players = playerService.getAllPlayers();
            logger.info("成功取得 {} 筆玩家資料", players.size());
            
            model.addAttribute("players", players);
            model.addAttribute("lastUpdated", new java.util.Date());
            return "players";
        } catch (Exception e) {
            logger.error("取得玩家資料時發生錯誤：", e);
            model.addAttribute("errorMessage", "無法取得玩家資料，請稍後再試");
            model.addAttribute("players", Collections.emptyList());
            return "players";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        logger.error("控制器發生未預期錯誤：", ex);
        model.addAttribute("errorMessage", "系統發生錯誤，請聯繫管理員");
        return "error"; // 需建立 error.html 模板
    }
}
