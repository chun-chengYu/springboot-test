package com.example.demo.command;

import com.example.demo.model.GameContext;
import com.example.demo.model.Player;
// import com.example.demo.model.Room; // 如果 currentRoom 確實不再需要，這個 import 也可以移除

public class UseItemCommand implements Command {
    private String itemName;

    public UseItemCommand(String itemName) {
        this.itemName = itemName != null ? itemName.toLowerCase() : "";
    }

    @Override
    public void execute(Player player, GameContext context) {
        // Room currentRoom = context.getCurrentRoom(); // <<< 刪除或註解掉這一行

        if (itemName.isEmpty()) {
            context.addMessage("請指定要使用的物品 (例如: use potion)。");
            return;
        }

        String targetItemInInventory = "healing_potion";

        if ("potion".equals(itemName)) {
            if (player.getItemQuantity(targetItemInInventory) > 0) {
                if (player.getHp() >= player.getMaxHp()) {
                    context.addMessage("你的HP已滿，無需治療。");
                } else {
                    int healAmount = 30;
                    int actualHeal = Math.min(healAmount, player.getMaxHp() - player.getHp());
                    player.heal(healAmount);
                    player.removeItem(targetItemInInventory, 1);
                    context.addMessage("你喝下治療藥水，回復 " + actualHeal + " HP！");
                    context.addMessage("你的 HP：" + player.getHp());
                }
            } else {
                context.addMessage("你身上沒有治療藥水。");
            }
        }
        /*
        // 如果將來您想讓 "use" 指令也能夠與房間內的物品互動，
        // 例如，檢查房間是否有某個可交互的物品（而不僅僅是藥水），
        // 那麼您可能需要重新引入 currentRoom 並使用它。
        // 例如：
        else if ("lever".equals(itemName) && currentRoom != null && currentRoom.hasObject("lever")) {
             currentRoom.interactWith("lever");
             context.addMessage("你拉下了控制桿！");
        }
        */
        else {
            context.addMessage("你無法使用 '" + itemName + "'，或者它不是一個可直接使用的物品。");
        }
    }
}
