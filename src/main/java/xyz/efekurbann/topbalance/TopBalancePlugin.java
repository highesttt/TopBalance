package xyz.efekurbann.topbalance;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.efekurbann.inventory.InventoryAPI;
import xyz.efekurbann.topbalance.commands.MainCommand;
import xyz.efekurbann.topbalance.objects.TopPlayer;
import xyz.efekurbann.topbalance.tasks.UpdateTop;
import xyz.efekurbann.topbalance.utils.ConfigManager;
import xyz.efekurbann.topbalance.utils.VaultManager;

import java.util.HashMap;

public final class TopBalancePlugin extends JavaPlugin {

    private static TopBalancePlugin instance;
    private final HashMap<Integer, TopPlayer> players = new HashMap<>();
    private InventoryAPI inventoryAPI;
    private UpdateTop updateTopTask;

    @Override
    public void onEnable() {
        instance = this;
        if (!VaultManager.setupEconomy()) {
            getLogger().severe("Vault-Eco not found! Plugin is disabled..");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        inventoryAPI = new InventoryAPI(this);

        ConfigManager.load("config.yml");

        updateTopTask = new UpdateTop(this);
        updateTopTask.runTaskTimerAsynchronously(this, 10L, 12000L);

        getCommand("baltop").setExecutor(new MainCommand(this));
        new Metrics(this, 11047);
        this.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                if (!player.hasPermission("topbalance.admin")) return;
            }
        }, this);

    }

    public InventoryAPI getInventoryAPI() {
        return inventoryAPI;
    }

    @Override
    public void onDisable() {

    }

    public HashMap<Integer, TopPlayer> getPlayersMap() {
        return players;
    }

    public UpdateTop getUpdateTopTask() {
        return updateTopTask;
    }

    public static TopBalancePlugin getInstance() {
        return instance;
    }

}
