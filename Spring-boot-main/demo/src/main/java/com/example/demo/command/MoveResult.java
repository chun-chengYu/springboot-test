package com.example.demo.command;

import com.example.demo.model.Room;
import java.util.List;

public class MoveResult implements CommandResult {
    private List<String> messages;
    private boolean success;
    private Room newRoom;

    public MoveResult(List<String> messages, boolean success, Room newRoom) {
        this.messages = messages;
        this.success = success;
        this.newRoom = newRoom;
    }

    // 加入 getters 和 setters
    public List<String> getMessages() {
        return messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public Room getNewRoom() {
        return newRoom;
    }
}
