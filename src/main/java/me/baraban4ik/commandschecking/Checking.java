package me.baraban4ik.commandschecking;


import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checking implements Listener {

    private CommandsChecking pl;
    private Economy econ;

    public Checking(CommandsChecking pl, Economy econ) {
        this.pl = pl;
        this.econ = econ;
    }

    static String color(String s, CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                s = PlaceholderAPI.setPlaceholders(p, s);
            }
        }
        if (CommandsChecking.getVersion() >= 1.16f) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher match = pattern.matcher(s);

            while (match.find()) {
                String color = s.substring(match.start(), match.end());
                s = s.replace(color, ChatColor.of(color) + "");
                match = pattern.matcher(s);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @EventHandler
    public void checkCommands (PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        FileConfiguration cfg = pl.getConfig();
        Location coord;
        int distance;
        String[] message = e.getMessage().replace("/", "").split(" ");
        List<String> actions;
        List<String> denyActions;

        for (String key : cfg.getConfigurationSection("Commands").getKeys(false)) {
            if (message[0].contains(key)) {
                World w = Bukkit.getWorld(cfg.getString("Commands." + key + ".coord.world"));
                int x = cfg.getInt("Commands." + key + ".coord.x");
                int y = cfg.getInt("Commands." + key + ".coord.y");
                int z = cfg.getInt("Commands." + key + ".coord.z");

                coord = new Location(w, x, y, z);
                distance = cfg.getInt("Commands." + key + ".radius");
                actions = cfg.getStringList("Commands." + key + ".actions");
                denyActions = cfg.getStringList("Commands." + key + ".deny-actions");

                int distanceSq = distance * distance;

                if (player.getLocation().distanceSquared(coord) <= distanceSq) {
                    Action(actions, player);
                }
                else {
                    e.setCancelled(true);
                    Action(denyActions, player);
                }
            }
        }
    }



    private void Action(List<String> str, Player p) {

        for (String line : str) {
            if (line.startsWith("(msg)")) {
                p.sendMessage(color(line.split("\\(msg\\)")[1], p));
            } else if (line.startsWith("(cmd)")) {
                ConsoleCommandSender console = pl.getServer().getConsoleSender();
                Bukkit.dispatchCommand(console, line.split("\\(cmd\\)")[1]);
            } else if (line.startsWith("(broadcastMsg)")) {
                Bukkit.broadcastMessage(color(line.split("\\(broadcastMsg\\)")[1], p));
            } else if (line.startsWith("(teleport)")) {
                line = line.split("\\(teleport\\)")[1];
                World w = Bukkit.getWorld(line.split(",")[0]);
                Location tpLoc = new Location(w, Integer.parseInt(line.split(",")[1]), Integer.parseInt(line.split(",")[2]), Integer.parseInt(line.split(",")[3]));
                p.teleport(tpLoc);
            } else if (line.startsWith("(take-item)")) {
                line = line.split("\\(take-item\\)")[1];
                ItemStack item = new ItemStack(Material.getMaterial(line.split(";")[0].toUpperCase()), Integer.parseInt(line.split(";")[1]));

                p.getInventory().removeItem(item);
            } else if (line.startsWith("(give-item)")) {
                line = line.split("\\(take-item\\)")[1];
                ItemStack item = new ItemStack(Material.getMaterial(line.split(";")[0].toUpperCase()), Integer.parseInt(line.split(";")[1]));

                p.getInventory().addItem(item);
            } else if (line.startsWith("(take-money)")) {
                if (pl.setupEconomy()) {
                    EconomyResponse r = econ.withdrawPlayer(p, Double.parseDouble(line.split("\\(take-money\\)")[1]));
                    if (r.transactionSuccess()) {
                        p.sendMessage(color(String.format(pl.getConfig().getString("Messages.take-money"), econ.format(r.amount), econ.format(r.balance)), p));
                    } else {
                        p.sendMessage(color(String.format(pl.getConfig().getString("Messages.money-error"), r.errorMessage), p));
                    }
                }
            } else if (line.startsWith("(give-money)")) {
                if (pl.setupEconomy()) {
                    EconomyResponse r = econ.depositPlayer(p, Double.parseDouble(line.split("\\(give-money\\)")[1]));
                    if (r.transactionSuccess()) {
                        p.sendMessage(color(String.format(pl.getConfig().getString("Messages.give-money"), econ.format(r.amount), econ.format(r.balance)), p));
                    } else {
                        p.sendMessage(color(String.format(pl.getConfig().getString("Messages.money-error"), r.errorMessage), p));
                    }
                }
            }
        }
    }
}
