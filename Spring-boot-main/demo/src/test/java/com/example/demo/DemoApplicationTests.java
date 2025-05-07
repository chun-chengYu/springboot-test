package com.example.demo;

import com.example.demo.model.Player;
import com.example.demo.repository.PlayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DemoApplicationTests {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void testSaveAndLoadPlayer() {
        // 建立玩家
        Player player = new Player();
        player.setName("勇者");
        player.setHp(100);
        player.setMaxHp(100);
        player.setAttack(15);
        player.setSkillNames(List.of("fireball", "heal"));
        player.setTotalDamage(0);
        player.setKillCount(0);

        // 儲存玩家
        Player saved = playerRepository.save(player);

        // 列印儲存後的資料
        printPlayerInfo("儲存後的玩家資料：", saved);

        // 讀取玩家
        Player loaded = playerRepository.findById(saved.getId()).orElse(null);
        assertThat(loaded).isNotNull();

        // 列印讀取後的資料
        printPlayerInfo("讀取後的玩家資料：", loaded);

        // 驗證資料
        assertThat(loaded.getName()).isEqualTo("勇者");
        assertThat(loaded.getSkillNames()).containsExactly("fireball", "heal");
        assertThat(loaded.getHp()).isEqualTo(100);
        assertThat(loaded.getMaxHp()).isEqualTo(100);
        assertThat(loaded.getAttack()).isEqualTo(15);
    }

    private void printPlayerInfo(String title, Player player) {
        System.out.println(title);
        System.out.println("ID: " + player.getId());
        System.out.println("Name: " + player.getName());
        System.out.println("HP: " + player.getHp());
        System.out.println("MaxHP: " + player.getMaxHp());
        System.out.println("Attack: " + player.getAttack());
        System.out.println("SkillNames: " + player.getSkillNames());
        System.out.println("TotalDamage: " + player.getTotalDamage());
        System.out.println("KillCount: " + player.getKillCount());
        System.out.println("-----------------------------");
    }

    @AfterEach
    void tearDown() {
        playerRepository.deleteAll();
    }
}
