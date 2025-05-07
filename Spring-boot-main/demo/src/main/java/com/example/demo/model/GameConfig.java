package com.example.demo.model;


import com.example.demo.command.HealSkill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class GameConfig {

    @Bean
    public List<Room> allGameRooms() {
        Room forest = new Room("forest_entrance", "森林入口", "你站在茂密森林的邊緣，前方隱約可見一條小徑。", new Monster("哥布林", 30, 8), true);
        Room temple = new Room("temple_hall", "廢棄神殿大廳", "宏偉但已殘破的大廳，光線從頂部的巨大裂縫灑下，照亮了塵封的祭壇。", new Monster("亡靈戰士", 50, 12), false);
        Room cave = new Room("dark_cave", "黑暗洞穴", "潮濕陰暗的洞穴，鐘乳石上滴著水，空氣中瀰漫著一股土腥味。", new Monster("巨型蜘蛛", 40, 10), true);

        forest.setExit("north", temple);
        forest.setExit("east", cave);
        temple.setExit("south", forest);
        cave.setExit("west", forest);

        return Arrays.asList(forest, temple, cave);
    }

    @Bean
    public List<Skill> allGameSkills() {
        return Arrays.asList(new FireballSkill(), new HealSkill());
    }

    @Bean
    public SkillRegistry skillRegistry(List<Skill> allSkills) {
        SkillRegistry registry = new SkillRegistry();
        allSkills.forEach(registry::registerSkill);
        return registry;
    }
}
