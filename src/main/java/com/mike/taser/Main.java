package com.mike.taser;

import com.mike.taser.Events.GeneralEvents;
import com.mike.taser.Events.Ricarica.RicaricaListener;
import com.mike.taser.Events.Taser.TaserListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance(){
        return instance;
    }

    public int getMaxCariche(){
        return this.getConfig().getInt("taser.cariche_massime");
    }

    @Override
    public void onEnable() {
        instance = this;
        long start = System.currentTimeMillis();
        getLogger().info(" ");
        getLogger().info("   ╔══════════════════════════════════════╗");
        getLogger().info("   ║  Plugin Taser.jar - Loading...  ║");
        getLogger().info("   ╚══════════════════════════════════════╝");

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        sanityCheckConfig();

        LogsManager.init();

        Bukkit.getPluginManager().registerEvents(new RicaricaListener(), this);
        Bukkit.getPluginManager().registerEvents(new TaserListener(), this);
        Bukkit.getPluginManager().registerEvents(new GeneralEvents(), this);

        getCommand("taser").setExecutor(new Command(this));
        getCommand("taser").setTabCompleter(new TabCompleter());

        if (Bukkit.getPluginManager().getPlugin("QualityArmory") == null) {
            getLogger().warning("QualityArmory NON trovato. Le armi non verranno bloccate correttamente durante il taser.");
        }

        getLogger().info("Taser.jar (by t.me/mike_workshop) attivato con successo (" + (System.currentTimeMillis() - start) + " ms)");
        getLogger().info(" ");
    }

    private void sanityCheckConfig(){
        if(!getConfig().isInt("taser.cariche_massime")){
            getLogger().severe("Config corrotta! taser.cariche_massime non esiste. Il plugin verrà disabilitato.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        int cariche = getConfig().getInt("taser.cariche_massime");
        if(cariche <= 0){
            getLogger().warning("Hai messo cariche_massime <= 0 in config. IL TASER NON SARÀ UTILIZZABILE");
        }
    }
}
