package net.danh.miningcontest;

import net.danh.miningcontest.CMD.Command;
import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Listener.BlockBreak;
import net.danh.miningcontest.Listener.JoinQuit;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        Mining.dataI.put("start", FileManager.getConfig().getInt("settings.contest_delay"));
        Mining.dataI.put("end", 0);
        (new BukkitRunnable() {
            public void run() {
                int start;
                if (Mining.dataI.get("start") > 0) {
                    start = Mining.dataI.get("start");
                } else {
                    start = FileManager.getConfig().getInt("settings.contest_delay");
                }
                Mining.dataI.replace("start", --start);
            }
        }).runTaskTimer(miningContest, 20L, 20L);
        (new BukkitRunnable() {
            public void run() {
                int end;
                if (Mining.dataI.get("end") > 0) {
                    end = Mining.dataI.get("end");
                    Mining.dataI.replace("end", --end);
                }
            }
        }).runTaskTimer(miningContest, 20L, 20L);
        (new BukkitRunnable() {
            public void run() {
                if (getServer().getOnlinePlayers().size() >= FileManager.getConfig().getInt("min_player")) {
                    if (!Mining.data.get("start")) {
                        Mining.StartMining();
                        Mining.dataI.replace("start", FileManager.getConfig().getInt("settings.contest_delay"));
                    }
                } else {
                    Bukkit.getServer().broadcastMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_cancel")).replace("#min#", String.valueOf(FileManager.getConfig().getInt("min_player"))).replace("#online#", String.valueOf(Bukkit.getOnlinePlayers().size()))));
                }
            }
        }).runTaskTimer(miningContest, FileManager.getConfig().getInt("settings.contest_delay") * 20L, FileManager.getConfig().getInt("settings.contest_delay") * 20L);
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

    public static String getTime(int secondsx) {
        int days = (int) TimeUnit.SECONDS.toDays(secondsx);
        int hours = (int) (TimeUnit.SECONDS.toHours(secondsx) - TimeUnit.DAYS.toHours(days));
        int minutes = (int) (TimeUnit.SECONDS.toMinutes(secondsx) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days));
        int seconds = (int) (TimeUnit.SECONDS.toSeconds(secondsx) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));

        if (days != 0) {
            return days + " " + FileManager.getConfig().getString("times.day") + " " + hours + " " + FileManager.getConfig().getString("times.hour") + " " + minutes + " " + FileManager.getConfig().getString("times.minute") + " " + seconds + " " + FileManager.getConfig().getString("times.second");
        } else {
            if (hours != 0) {
                return hours + " " + FileManager.getConfig().getString("times.hour") + " " + minutes + " " + FileManager.getConfig().getString("times.minute") + " " + seconds + " " + FileManager.getConfig().getString("times.second");
            } else {
                if (minutes != 0) {
                    return minutes + " " + FileManager.getConfig().getString("times.minute") + " " + seconds + " " + FileManager.getConfig().getString("times.second");
                } else {
                    return seconds + " " + FileManager.getConfig().getString("times.second");
                }
            }

        }
    }
}
