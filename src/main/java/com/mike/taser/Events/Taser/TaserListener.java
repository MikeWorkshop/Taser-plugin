package com.mike.taser.Events.Taser;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mike.taser.Items;
import com.mike.taser.LogsManager;
import com.mike.taser.Main;
import me.zombie_striker.qg.api.QAWeaponPrepareShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TaserListener implements Listener {

    private static int COOLDOWN_SECONDS = 5;
    private static Cache<UUID, Long> taserTime = CacheBuilder.newBuilder().expireAfterWrite(COOLDOWN_SECONDS, TimeUnit.SECONDS).build();

    public static boolean isTased(Player p) {
        return taserTime.getIfPresent(p.getUniqueId()) != null;
    }

    @EventHandler(priority = EventPriority.NORMAL ,ignoreCancelled = true)
    public void onTaser(TaserEvent e){
        Player player = e.getAttacker();
        Player target = e.getVictim();

        String adminPerm = Main.getInstance().getConfig().getString("permesso_admin");
        if (adminPerm != null && target.hasPermission(adminPerm)) {
            player.sendMessage(ChatColor.RED + "Non puoi taserare lo staff");
            return;
        }

        if (target.isBlocking()) {
            Vector direction = target.getLocation().getDirection();
            Vector toAttacker = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
            double dot = direction.dot(toAttacker);

            boolean attackerInFront = dot > 0.3;

            if (attackerInFront) {
                player.sendMessage(ChatColor.RED + "Non puoi taserare qualcuno che si sta scudando");
                return;
            }
        }

        int cariche = Items.getNumeroCariche(player.getInventory().getItemInMainHand());
        if (cariche != -1){
            if (cariche > 0){
                int nuoveCariche = cariche-1;
                ItemStack nuovoTaser = Items.taser(nuoveCariche);
                player.getInventory().setItemInMainHand(nuovoTaser);
            }else{
                player.sendMessage(ChatColor.RED + "Il taser che stai tentando di utilizzare è scarico");
                return;
            }
        }
        taserTime.put(target.getUniqueId(), System.currentTimeMillis() + (COOLDOWN_SECONDS*1000));

        int effectSeconds = 5;
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectSeconds*20, 1));
        target.sendTitle(ChatColor.BOLD.toString() + ChatColor.YELLOW + "BzBzBzBzBzBzBz", null, 10, COOLDOWN_SECONDS*20, 20);
        String sound = Main.getInstance().getConfig().getString("taser.suono_taser");
        target.getWorld().playSound(target.getLocation(), sound, 1.0f, 1.0f);

        LogsManager.logTaserata(player.getName(), target.getName(), player.getLocation());
    }

    @EventHandler
    public void onRightClickPlayer(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player target)) return;
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        if (isTased(player)) { e.setCancelled(true); return;}
        if (isTased(target)) { e.setCancelled(true); return;}

        ItemStack playersTool = player.getInventory().getItemInMainHand();
        if (!Items.isTaser(playersTool)) return;

        TaserEvent event = new TaserEvent(player, target);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (isTased(p)) { e.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSwitch(PlayerItemHeldEvent e) {
        if (isTased(e.getPlayer())) { e.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (isTased(p)) { e.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player p)) return;
        if (isTased(p)) { e.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (isTased(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cSei taserato, non puoi parlare.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (isTased(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cSei taserato, non puoi eseguire comandi.");
        }
    }

    @EventHandler
    public void onShot(QAWeaponPrepareShootEvent e){
        Player player = e.getPlayer();
        ItemStack playersTool = player.getInventory().getItemInMainHand();
        if (playersTool == null) return;

        if (taserTime.asMap().containsKey(player.getUniqueId())){
            e.setCancelled(true);
            player.sendMessage("§cNon puoi sparare da taserato");
        }
    }
}
