package com.example.demo.repository;

import com.example.demo.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // 可以添加根據玩家名稱查找的方法等
    // Optional<Player> findByName(String name);
}
