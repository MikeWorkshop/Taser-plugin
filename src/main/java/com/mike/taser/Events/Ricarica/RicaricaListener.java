package com.mike.taser.Events.Ricarica;

import com.mike.taser.Items;
import com.mike.taser.LogsManager;
import com.mike.taser.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RicaricaListener implements Listener {
    private static final Main main = Main.getInstance();

    @EventHandler
    public void onRicarica(RicaricaEvent e){
        if (e.isCancelled()) return;
        int caricheMassime = main.getConfig().getInt("taser.cariche_massime");

        Player player = e.getPlayer();
        ItemStack playersTool = player.getInventory().getItemInMainHand();

        int carichePresenti = Items.getNumeroCariche(playersTool);
        if (carichePresenti != -1){
            if (carichePresenti == caricheMassime){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aQuesto taser è già carico"));
            } else {
                int caricheNecessarie = caricheMassime - carichePresenti;

                for (ItemStack item : player.getInventory().getContents()) {
                    if (caricheNecessarie <= 0 || carichePresenti == caricheMassime) break;
                    if (item == null) continue;
                    if (!Items.isRicarica(item)) continue;

                    item.setAmount(item.getAmount() - 1);
                    caricheNecessarie--;
                    carichePresenti++;

                    if (item.getAmount() <= 0) {
                        player.getInventory().remove(item);
                    }
                }

                if (carichePresenti != Items.getNumeroCariche(playersTool)) {
                    player.getInventory().setItemInMainHand(Items.taser(carichePresenti));
                    String sound = Main.getInstance().getConfig().getString("taser.suono_ricarica");
                    player.getWorld().playSound(player.getLocation(), sound, 1.0f, 1.0f);

                    LogsManager.logRicarica(player.getName(), player.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent e){
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
            Player player = e.getPlayer();
            ItemStack playersTool = player.getInventory().getItemInMainHand();
            if (Items.isTaser(playersTool)){
                RicaricaEvent event = new RicaricaEvent(player);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }
}
