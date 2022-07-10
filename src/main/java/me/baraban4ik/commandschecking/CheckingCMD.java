package me.baraban4ik.commandschecking;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CheckingCMD implements CommandExecutor {

    private final CommandsChecking pl;

    public CheckingCMD(CommandsChecking  pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("cmdcheck.reload")) {
                sender.sendMessage(Checking.color(pl.getConfig().getString("Messages.no-permission"), sender));
            }
            pl.reloadConfig();
            sender.sendMessage(Checking.color(pl.getConfig().getString("Messages.reload"), sender));
        }
        else {
            if (sender.hasPermission("cmdcheck.info")) {
                List<String> help = pl.getConfig().getStringList("Messages.info");
                help.forEach((x) -> sender.sendMessage(Checking.color(x, sender)));
            }
        }
        return true;
    }
}
