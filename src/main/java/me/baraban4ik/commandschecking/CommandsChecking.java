package me.baraban4ik.commandschecking;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsChecking extends JavaPlugin {
    private static Economy econ = null;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        System.out.println("");
        System.out.println("               §6Commands Checking");
        System.out.println("");
        System.out.println("                 Version §8- §6" + this.getDescription().getVersion());
        System.out.println("              Author §8- §6Baraban4ik");
        System.out.println("");
        System.out.println("               §6Commands Checking");
        System.out.println("");

        getServer().getPluginManager().registerEvents(new Checking(this, econ),this);
        getServer().getPluginCommand("cmdcheck").setExecutor(new CheckingCMD(this));

    }


    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Float getVersion() {
        String version = Bukkit.getVersion();
        String pattern = "[^0-9\\.\\:]";
        String versionMinecraft = version.replaceAll(pattern, "");
        return Float.parseFloat(versionMinecraft.substring(versionMinecraft.indexOf(":") + 1, versionMinecraft.lastIndexOf(".")));
    }
}
