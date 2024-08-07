package xyz.efekurbann.topbalance.menus;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;
import xyz.efekurbann.inventory.InventoryAPI;
import xyz.efekurbann.topbalance.TopBalancePlugin;
import xyz.efekurbann.topbalance.objects.TopPlayer;
import xyz.efekurbann.topbalance.utils.ConfigManager;
import xyz.efekurbann.topbalance.utils.Tools;
import xyz.efekurbann.topbalance.utils.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopMenu extends GUI {

    private final TopBalancePlugin plugin;
    private final FileConfiguration config = ConfigManager.get("config.yml");

    public TopMenu(InventoryAPI api, String id, String title, int size) {
        super(api, id, title, size);
        this.plugin = TopBalancePlugin.getInstance();

        create();
    }

    public void create() {

        if (config.getBoolean("Gui.items.fill.enabled")){
            for (int i = 0; i < getSize(); i++){
                addItem(i, new ItemBuilder(
                        XMaterial.matchXMaterial(config.getString("Gui.items.fill.material").toUpperCase(Locale.ENGLISH)).get().parseItem()).withName(" ").build());
            }
        }

        for (String key : config.getConfigurationSection("Tops").getKeys(false)) {
            int rank = config.getInt("Tops." + key + ".rank");
            TopPlayer player = plugin.getPlayersMap().get(rank-1);
            int slot = config.getInt("Tops." + key + ".slot");
            ItemStack item;
            if (player != null) {
                if (config.getBoolean("Gui.custom-item")) {
                    List<String> lore = new ArrayList<>();

                    for (String str : config.getStringList("Gui.items.player-item.lore")) {
                        lore.add(str.replace("{rank}", String.valueOf(rank))
                                .replace("{name}", player.getName())
                                .replace("{bank}", Tools.formatMoney(player.getBank()))
                                .replace("{balance}", Tools.formatMoney(player.getBalance())));
                    }
                    item = new ItemBuilder(XMaterial.valueOf(config.getString("Tops." + key + ".material").toUpperCase(Locale.ENGLISH)).parseMaterial())
                            .withName(config.getString("Gui.items.player-item.name")
                                    .replace("{rank}", String.valueOf(rank))
                                    .replace("{name}", player.getName()))
                            .withLore(lore).build();
                } else {
                    item = getSkull(slot, rank-1, "player-item", false);
                }
            } else {
                item = new ItemBuilder(XMaterial.valueOf(config.getString("Gui.items.player-not-found.material").toUpperCase(Locale.ENGLISH)).parseMaterial())
                        .withName(config.getString("Gui.items.player-not-found.name"))
                        .withLore(config.getStringList("Gui.items.player-not-found.lore")).build();
                addItem(slot, item);
            }
        }


        addItem(
                config.getInt("Gui.items.close-menu.slot"),
                new Hytem(new ItemBuilder(XMaterial.valueOf(config.getString("Gui.items.close-menu.material").toUpperCase(Locale.ENGLISH)).parseMaterial())
                        .withName(config.getString("Gui.items.close-menu.name"))
                        .withLore(config.getStringList("Gui.items.close-menu.lore")).build(), (event) ->{
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        event.getWhoClicked().closeInventory();
                    }, 2);
                })
        );

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (config.getBoolean("Messages.gui-opened.status"))
            event.getPlayer().sendMessage(Tools.colored(config.getString("Messages.gui-opened.message")));

        Player player = (Player) event.getPlayer();

        Tools.getPosition(event.getPlayer().getName()).whenCompleteAsync((selfRank, throwable) -> {
            TopPlayer topPlayer = plugin.getPlayersMap().get(selfRank);

            if (topPlayer == null) return;
            List<String> lore = new ArrayList<>();
            for (String str : config.getStringList("Gui.items.self-item.lore")) {
                lore.add(str.replace("{rank}", String.valueOf(selfRank+1))
                        .replace("{name}", player.getName())
                        .replace("{bank}", Tools.formatMoney(topPlayer.getBank()))
                        .replace("{balance}", Tools.formatMoney(topPlayer.getBalance())));
            }

            if (config.getBoolean("Gui.custom-item")) {
                addItem(config.getInt("Gui.items.self-item.slot"), new ItemBuilder(
                        XMaterial.matchXMaterial(config.getString("Gui.items.self-item.material").toUpperCase(Locale.ENGLISH)).get().parseItem())
                        .withName(config.getString("Gui.items.self-item.name"))
                        .withLore(lore).build());
            } else {
                getSkull(config.getInt("Gui.items.self-item.slot"), selfRank, "self-item", true);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public ItemStack getSkull(Integer slot, Integer number, String path, boolean executor) {
        TopPlayer player = plugin.getPlayersMap().get(number);

        
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        // run async task
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    skullMeta.setOwner(UUIDFetcher.getName(player.getUUID()));
                    item.setItemMeta(skullMeta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // continue sync:
                if (number < 11 || executor == true) {
                    skullMeta.setDisplayName(Tools.colored(config.getString("Gui.items." + path + ".name")
                            .replace("{rank}", String.valueOf(number + 1))
                            .replace("{name}", UUIDFetcher.getName(player.getUUID()))
                            .replace("{bank}", Tools.formatMoney(player.getBank()))
                            .replace("{balance}", Tools.formatMoney(player.getBalance()))));
                    List<String> lore = new ArrayList<>();
                    for (String str : config.getStringList("Gui.items." + path + ".lore")) {
                        lore.add(str.replace("{rank}", String.valueOf(number + 1))
                                .replace("{name}", UUIDFetcher.getName(player.getUUID()))
                                .replace("{bank}", Tools.formatMoney(player.getBank()))
                                .replace("{balance}", Tools.formatMoney(player.getBalance())));
                    }
                    skullMeta.setLore(Tools.colored(lore));

                    item.setItemMeta(skullMeta);

                    addItem(slot, item);
                }
            }
        });
        // run after async task

        //long start = System.currentTimeMillis();
        //long startNano = System.nanoTime();
        //System.out.println("[DEBUG] Took " + (System.currentTimeMillis() - start) + "ms");
        //System.out.println("[DEBUG] Took " + (System.nanoTime() - startNano) + " nano sec");

        //start = System.currentTimeMillis();
        //startNano = System.nanoTime();
        //System.out.println("[DEBUG] (2) Took " + (System.currentTimeMillis() - start) + "ms");
        //System.out.println("[DEBUG] (2) Took " + (System.nanoTime() - startNano) + " nano sec");
        return item;
    }

}
