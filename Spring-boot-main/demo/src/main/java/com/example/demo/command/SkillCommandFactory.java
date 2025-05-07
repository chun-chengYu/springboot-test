package com.example.demo.command;

import com.example.demo.model.SkillRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SkillCommandFactory {
    private final SkillRegistry skillRegistry;

    @Autowired
    public SkillCommandFactory(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    public SkillCommand create(String skillName) {
        // SkillCommand 的建構子仍然是 (String, SkillRegistry)
        return new SkillCommand(skillName, skillRegistry);
    }
}
