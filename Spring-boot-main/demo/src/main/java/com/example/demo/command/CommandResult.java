package com.example.demo.command;

import java.util.List;

public interface CommandResult {
    // 可以是空的
    List<String> getMessages();
}
