package com.mike.taser;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Items {

    private static NamespacedKey taserKey = new NamespacedKey(Main.getInstance(), "taser");
    private static NamespacedKey ricaricaKey = new NamespacedKey(Main.getInstance(), "ricarica_taser");
    private static final Main main = Main.getInstance();
    private static final FileConfiguration config = main.getConfig();


    public static ItemStack taser(int ricariche){
        String materialS = config.getString("taser.item");
        Material material = Material.matchMaterial(materialS);
        if (material == null) material = Material.STICK;

        ItemStack taser = new ItemStack(material);
        ItemMeta meta = taser.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                config.getString("taser.nome")));

        int caricheMassime = config.getInt("taser.cariche_massime", 3);
        int max = caricheMassime;

        List<String> loreCfg = config.getStringList("taser.lore");
        List<String> lore = loreCfg.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&',
                        line.replace("%current%", String.valueOf(ricariche))
                                .replace("%max%", String.valueOf(max))
                ))
                .toList();
        meta.setLore(lore);

        meta.setCustomModelData(config.getInt("taser.cmdata"));

        meta.getPersistentDataContainer().set(taserKey, PersistentDataType.INTEGER, ricariche);

        taser.setItemMeta(meta);
        return taser;
    }


    public static ItemStack ricarica(){
        String materialS = config.getString("ricariche.item");
        Material material = Material.matchMaterial(materialS);
        if (material == null) material = Material.PHANTOM_MEMBRANE;

        ItemStack ricarica = new ItemStack(material);
        ItemMeta meta = ricarica.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("ricariche.nome")));

        List<String> loreCfg = config.getStringList("ricariche.lore");
        if (!loreCfg.isEmpty()) {
            List<String> lore = loreCfg.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .toList();
            meta.setLore(lore);
        }

        meta.setCustomModelData(config.getInt("ricariche.cmdata"));

        meta.getPersistentDataContainer().set(ricaricaKey, PersistentDataType.STRING, "ricarica_taser");

        ricarica.setItemMeta(meta);
        return ricarica;
    }

    public static boolean isRicarica(ItemStack item){
        return item != null && item.hasItemMeta() &&
                item.getItemMeta().getPersistentDataContainer().has(ricaricaKey, PersistentDataType.STRING);
    }

    public static boolean isTaser(ItemStack item){
        return item != null && item.hasItemMeta() &&
                item.getItemMeta().getPersistentDataContainer().has(taserKey, PersistentDataType.INTEGER);
    }

    public static int getNumeroCariche(ItemStack item){
        if (!isTaser(item)) return -1;
        return item.getItemMeta().getPersistentDataContainer().get(taserKey, PersistentDataType.INTEGER);
    }

}
