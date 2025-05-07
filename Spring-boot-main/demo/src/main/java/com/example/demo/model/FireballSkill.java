package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component // 加上這個註解，讓 Spring Boot 能識別這個類別為 Bean
public class FireballSkill implements Skill {
    @Override
    public String getName() { return "fireball"; }

    @Override
    public List<String> use(Player player, Monster monster) {
        List<String> messages = new ArrayList<>();
        messages.add("🔥 你施放了【火球術】！");
        int damage = 40;
        monster.takeDamage(damage);
        player.addDamage(damage);
        messages.add("對 " + monster.getName() + " 造成 " + damage + " 傷害！");
        return messages;
    }
}
