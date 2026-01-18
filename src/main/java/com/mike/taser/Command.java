package com.mike.taser;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {

    private Main main;

    public Command(Main main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String s, String[] args) {
        if (sender instanceof Player player){
            if (player.hasPermission(main.getConfig().getString("permesso_admin"))){
                if (args.length == 2){
                    if (args[0].equalsIgnoreCase("get")){
                        if (args[1].equalsIgnoreCase("taser")){
                            int cariche = main.getConfig().getInt("taser.cariche_massime");
                            if (cariche == 0) { System.out.println("Taser.jar: hai messo 0 cariche come cariche massime in config, quindi il taser non sarà utilizzabie"); return false; }
                            player.getInventory().addItem(Items.taser(cariche));
                            player.sendMessage(ChatColor.GREEN + "Hai ricevuto un taser");
                            LogsManager.logComando(player.getName(), "taser");
                        } else if (args[1].equalsIgnoreCase("ricarica")){
                            player.getInventory().addItem(Items.ricarica());
                            player.sendMessage(ChatColor.GREEN + "Hai ricevuto una ricarica taser");
                            LogsManager.logComando(player.getName(), "ricarica");
                        } else {
                            player.sendMessage(ChatColor.RED + "Utilizzo corretto: /taser get <taser / ricarica>");
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "Utilizzo corretto: /taser get <taser / ricarica>");
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "Utilizzo corretto: /taser get <taser / ricarica>");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Non hai il permesso necessario per eseguire questo comando.");
            }
        }
        return false;
    }
}
