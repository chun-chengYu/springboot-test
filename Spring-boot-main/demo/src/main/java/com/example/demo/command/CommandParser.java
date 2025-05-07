package com.example.demo.command;

import com.example.demo.model.Player; // 確保引入 Player
import com.example.demo.model.GameContext; // 確保引入 GameContext
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class CommandParser {

    private final Map<String, Function<String[], Command>> commandRegistry = new HashMap<>();
    private final SkillCommandFactory skillCommandFactory;
    // 假設您可能還有其他 Command 工廠，例如 UseItemCommandFactory 等
    // private final UseItemCommandFactory useItemCommandFactory; // 範例

    @Autowired
    public CommandParser(SkillCommandFactory skillCommandFactory
                         /* , UseItemCommandFactory useItemCommandFactory */ // 如果有多個工廠，確保逗號和參數正確
                        ) {
        this.skillCommandFactory = skillCommandFactory;
        // this.useItemCommandFactory = useItemCommandFactory; // 如果有其他工廠，則取消註解

        // 註冊指令
        commandRegistry.put("look", args -> new LookCommand());
        commandRegistry.put("attack", args -> new AttackCommand());
        commandRegistry.put("exit", args -> new ExitCommand());
        commandRegistry.put("heal", args -> new HealCommand()); // <<<--- 新增或確保此行存在

        commandRegistry.put("skill", args -> {
            if (args.length > 1) {
                // 使用 skillCommandFactory.create()，它會處理 SkillRegistry 的注入
                return this.skillCommandFactory.create(args[1]);
            }
            // 如果沒有提供技能名稱，返回一個提示用戶的 Command
            return (Player player, GameContext context) -> context.addMessage("請輸入技能名稱，例如：skill fireball");
        });

        commandRegistry.put("use", args -> {
            if (args.length > 1) {
                // 如果有 UseItemCommandFactory:
                // return this.useItemCommandFactory.create(args[1]);
                // 否則，直接創建:
                return new UseItemCommand(args[1]);
            }
            return (Player player, GameContext context) -> context.addMessage("請輸入物品名稱，例如：use potion");
        });

        commandRegistry.put("move", args -> {
            if (args.length > 1) {
                return new MoveCommand(args[1]);
            }
            return (Player player, GameContext context) -> context.addMessage("請輸入移動方向，例如：move north");
        });

        // 快捷移動指令
        commandRegistry.put("north", args -> new MoveCommand("north"));
        commandRegistry.put("south", args -> new MoveCommand("south"));
        commandRegistry.put("east", args -> new MoveCommand("east"));
        commandRegistry.put("west", args -> new MoveCommand("west"));
    }

    public Command parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            // 可以返回一個默認的無操作指令或提示指令
            return (Player player, GameContext context) -> context.addMessage("請輸入指令。");
        }
        String[] parts = input.trim().toLowerCase().split("\\s+");
        String commandWord = parts[0];

        Function<String[], Command> commandBuilder = commandRegistry.get(commandWord);
        if (commandBuilder != null) {
            // 將所有解析出來的部分 (parts) 傳遞給指令構建 Lambda
            return commandBuilder.apply(parts);
        }
        // 未知指令，返回一個提示指令
        return (Player player, GameContext context) -> context.addMessage("無效的指令：" + input);
    }
}
