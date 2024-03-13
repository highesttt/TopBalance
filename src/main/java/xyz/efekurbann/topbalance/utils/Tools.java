package xyz.efekurbann.topbalance.utils;

import org.bukkit.ChatColor;
import xyz.efekurbann.topbalance.TopBalancePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Tools {

    public static String colored(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> colored(List<String> lore) {
        List<String> list = new ArrayList<>();
        for (String str : lore){
            list.add(colored(str));
        }
        return list;
    }

    public static void reload(){
        ConfigManager.reload("config.yml");
    }

    public static String formatMoney(double money){
        return VaultManager.getEcon().format(money);
    }

    public static CompletableFuture<Integer> getPosition(String name) {
        return CompletableFuture.supplyAsync(() -> TopBalancePlugin.getInstance().getPlayersMap().entrySet()
                .parallelStream()
                .filter((entry) -> entry.getValue().getName().equals(name))
                .findAny()
                .map(Map.Entry::getKey)
                .orElse(TopBalancePlugin.getInstance().getPlayersMap().size()+1));
    }

}
