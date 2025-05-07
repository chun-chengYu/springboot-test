package com.example.demo.service;

import com.example.demo.model.Player;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // 確保引入 List

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player createNewPlayer(String name, String initialRoomId) {
        Player newPlayer = new Player();
        java.util.List<String> initialSkills = java.util.List.of("fireball", "heal");
        newPlayer.initialize(name, 100, 15, initialSkills, initialRoomId);
        return playerRepository.save(newPlayer);
    }

    @Transactional
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Transactional(readOnly = true)
    public Player loadPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    // vvVvv 新增這個方法 vVvvV
    /**
     * 獲取所有玩家的列表。
     * @return 包含所有 Player 物件的 List。
     */
    @Transactional(readOnly = true) // 通常查詢操作是只讀的
    public List<Player> getAllPlayers() {
        return playerRepository.findAll(); // JpaRepository 提供了 findAll() 方法
    }
    // ^^^^ 新增這個方法結束 ^^^^
}
