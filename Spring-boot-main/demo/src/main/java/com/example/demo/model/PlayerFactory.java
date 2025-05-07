package com.example.demo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerFactory {
    @Autowired
    private List<Skill> defaultSkills; // 注入所有標記為 @Component 的技能

    // 修改 createPlayer 方法以接收 initialRoomId
    public Player createPlayer(String name, int hp, int attack, String initialRoomId) {
        Player player = new Player();
        
        List<String> skillNames;
        if (defaultSkills != null) {
            skillNames = defaultSkills.stream()
                                      .map(Skill::getName) // 確保 Skill 介面有 getName() 方法
                                      .collect(Collectors.toList());
        } else {
            skillNames = new java.util.ArrayList<>(); // 如果沒有預設技能，則為空列表
        }
        
        // 傳遞第五個參數 initialRoomId
        player.initialize(name, hp, attack, skillNames, initialRoomId);
        return player;
    }
}
