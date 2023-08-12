package net.danh.miningcontest;

import net.danh.miningcontest.CMD.Command;
import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Listener.BlockBreak;
import net.danh.miningcontest.Listener.JoinQuit;
import net.danh.miningcontest.Manager.FileManager;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public final class MiningContest extends JavaPlugin {
    private static MiningContest miningContest;

    public static MiningContest getMiningContest() {
        return miningContest;
    }

    @Override
    public void onEnable() {
        miningContest = this;
        SimpleConfigurationManager.register(miningContest);
        FileManager.loadFiles();
        registerEvents(new BlockBreak(), new JoinQuit());
        new Command();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData.points.put(p.getName(), 0);
        }
        Mining.data.put("start", false);
        Mining.data.put("end", false);
        (new BukkitRunnable() {
            public void run() {
                if (getServer().getOnlinePlayers().size() >= 5) {
                    if (!Mining.data.get("start")) {
                        Mining.StartMining();
                    }
                }
            }
        }).runTaskTimer(this, FileManager.getConfig().getInt("settings.contest_delay") * 20L, FileManager.getConfig().getInt("settings.contest_delay") * 20L);
    }

    @Override
    public void onDisable() {
        FileManager.saveFiles();
        Mining.setCancelled(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData.points.put(p.getName(), 0);
        }
    }


    public void registerEvents(Listener... listeners) {
        Arrays.asList(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, miningContest));
    }
}
