package com.example.demo.model;

import org.springframework.stereotype.Component; // 如果 SkillRegistry 是 Spring 管理的 Bean

import java.util.HashMap;
import java.util.Map;
import java.util.List; // 引入 List
import java.util.ArrayList; // 引入 ArrayList


@Component // 通常 SkillRegistry 會是一個單例的 Spring Bean
public class SkillRegistry {
    private Map<String, Skill> skills = new HashMap<>();

    // 公開的無參構造函數（如果 Spring 需要）
    public SkillRegistry() {}

    /**
     * 註冊一個技能到註冊表中。
     * @param skill 要註冊的技能對象。
     */
    public void registerSkill(Skill skill) {
        if (skill != null && skill.getName() != null && !skill.getName().trim().isEmpty()) {
            skills.put(skill.getName().toLowerCase(), skill); // 建議技能名統一小寫存儲和查找
            System.out.println("技能已註冊: " + skill.getName());
        } else {
            System.err.println("警告：嘗試註冊一個無效的技能 (null 或名稱為空)。");
        }
    }

    /**
     * 根據技能名稱獲取技能對象。
     * @param name 技能的名稱 (不區分大小寫)。
     * @return 如果找到則返回 Skill 對象，否則返回 null。
     */
    public Skill getSkill(String name) {
        if (name == null) return null;
        return skills.get(name.toLowerCase());
    }

    /**
     * 獲取所有已註冊技能的名稱列表。
     * @return 包含所有技能名稱的 List<String>。
     */
    public List<String> getAllSkillNames() {
        return new ArrayList<>(skills.keySet());
    }
}
