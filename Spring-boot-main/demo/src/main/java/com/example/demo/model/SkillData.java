package com.example.demo.model;

public class SkillData {
    private String name;
    private int damage;
    private String description;
    // 可以添加其他技能屬性，如冷卻時間、魔法消耗等

    public SkillData(String name, int damage, String description) {
        this.name = name;
        this.damage = damage;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public String getDescription() {
        return description;
    }
    // 其他 getter
}
