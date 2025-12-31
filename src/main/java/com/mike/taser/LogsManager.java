package com.mike.taser;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogsManager {
    private static File ricaricheFile, taserateFile, comandiFile;
    private static FileConfiguration ricaricheCfg, taserateCfg, comandiCfg;
    private static boolean logsEnabled;

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void init() {
        logsEnabled = Main.getInstance().getConfig().getBoolean("logs_enabled");
        if (!logsEnabled) return;

        ricaricheFile = setupFile("ricariche_logs.yml");
        taserateFile = setupFile("taserate_logs.yml");
        comandiFile = setupFile("comandi_logs.yml");

        ricaricheCfg = YamlConfiguration.loadConfiguration(ricaricheFile);
        taserateCfg = YamlConfiguration.loadConfiguration(taserateFile);
        comandiCfg = YamlConfiguration.loadConfiguration(comandiFile);
    }

    private static File setupFile(String name) {
        File file = new File(Bukkit.getPluginManager().getPlugin("Taser").getDataFolder(), name);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ignored) {}
        }
        return file;
    }

    private static void save(FileConfiguration cfg, File file) {
        if (!logsEnabled) return;
        try { cfg.save(file); } catch (Exception e) { e.printStackTrace(); }
    }

    private static String locToString(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
                " in " + loc.getWorld().getName() + ")";
    }

    private static void write(FileConfiguration cfg, File file, String message) {
        String day = LocalDate.now().format(DATE);
        String time = LocalDateTime.now().format(TIME);

        // path tipo: "20-01-2025.14:22:01"
        cfg.set(day + "." + time, message);
        save(cfg, file);
    }

    public static void logRicarica(String player, Location loc) {
        if (!logsEnabled) return;
        String msg = player + " ha ricaricato un taser alle " +
                LocalDateTime.now().format(TIME) + " alla location " + locToString(loc);
        write(ricaricheCfg, ricaricheFile, msg);
    }

    public static void logTaserata(String player, String target, Location loc) {
        if (!logsEnabled) return;
        String msg = player + " ha taserato " + target + " alle " +
                LocalDateTime.now().format(TIME) + " alla location " + locToString(loc);
        write(taserateCfg, taserateFile, msg);
    }

    public static void logComando(String player, String cosa) {
        if (!logsEnabled) return;
        String msg;
        if (cosa.equalsIgnoreCase("taser"))
            msg = player + " ha creato un nuovo taser alle " + LocalDateTime.now().format(TIME);
        else if (cosa.equalsIgnoreCase("ricarica"))
            msg = player + " ha creato una nuova ricarica alle " + LocalDateTime.now().format(TIME);
        else
            msg = player + " ha eseguito il comando (" + cosa + ") alle " + LocalDateTime.now().format(TIME);

        write(comandiCfg, comandiFile, msg);
    }

}
