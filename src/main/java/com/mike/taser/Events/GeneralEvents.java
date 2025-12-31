package com.mike.taser.Events;

import com.mike.taser.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GeneralEvents implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission(Main.getInstance().getConfig().getString("permesso_admin"))) return;

        TextComponent mess = new TextComponent("§b§lTaser.jar" + " §7è un plugin gratuito creato dal canale telegram §b@Mike_Workshop");
        mess.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://t.me/mike_workshop"));
        mess.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Scopri il canale Mike Workshop su telegram")));
        player.spigot().sendMessage(mess);
    }
}
