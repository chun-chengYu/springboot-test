package com.example.demo.command;

import org.springframework.stereotype.Component;

@Component
public class MoveCommandFactory {
    public MoveCommand create(String direction) {
        return new MoveCommand(direction);
    }
}
