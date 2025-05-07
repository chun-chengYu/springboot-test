package com.example.demo.model;

// 如果 Monster 也需要持久化，則添加 JPA 註解
// @Entity
public class Monster {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 如果需要持久化，則添加 ID

    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;
    private boolean isDead;

    public Monster() {} // JPA 需要一個無參構造函數

    public Monster(String name, int hp, int attackPower) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.isDead = false;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        this.isDead = dead;
        if (this.isDead) {
            this.hp = 0;
        }
    }

    public void takeDamage(int damage) {
        if (isDead) return;
        this.hp = Math.max(0, this.hp - damage);
        if (this.hp == 0) {
            setDead(true);
        }
    }

   // 如果需要持久化，則添加 getter/setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
