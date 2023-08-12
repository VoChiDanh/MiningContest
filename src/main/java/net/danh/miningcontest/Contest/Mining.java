package net.danh.miningcontest.Contest;

import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.MiningContest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Mining {

    public static HashMap<String, Boolean> data = new HashMap<>();

    public static void setCancelled(boolean cancelled) {
        data.replace("end", cancelled);
    }

    public static void StartMining() {
        data.put("start", true);
        Bukkit.broadcastMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_start")));
        Bukkit.broadcastMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_warning"))
                .replace("#block#", String.valueOf(FileManager.getConfig().getInt("limit_blocks"))));
        for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
            PlayerData.points.put(p.getName(), 0);
        }
        (new BukkitRunnable() {
            int counter = FileManager.getConfig().getInt("settings.contest_times");

            public void run() {
                if (data.get("end")) {
                    cancel();
                    data.put("end", false);
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_end")));
                            }
                        }.runTask(MiningContest.getMiningContest());
                    }
                    Map<String, Integer> sortedMap = PlayerData.points.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    sortedMap.forEach((s, integer) -> {
                        if (integer >= FileManager.getConfig().getInt("limit_blocks")) {
                            Player p = Bukkit.getPlayer(s);
                            if (p != null) {
                                p.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer))));
                                List<String> player = new ArrayList<>(sortedMap.keySet());
                                boolean ontop = false;
                                for (int i = 0; i < 3; i++) {
                                    if (player.get(i).equalsIgnoreCase(s)) {
                                        ontop = true;
                                        List<String> cmd = FileManager.getConfig().getStringList("reward.top.top" + (i + 1)).stream().map(s1 -> s1.replace("#player#", s)).collect(Collectors.toList());
                                        cmd.forEach(c -> MiningContest.getMiningContest().getServer().dispatchCommand(Bukkit.getConsoleSender(), c));
                                    }
                                }
                                if (!ontop) {
                                    if (Bukkit.getPlayer(s) != null) {
                                        List<String> cmd = FileManager.getConfig().getStringList("reward.top.others").stream().map(s1 -> s1.replace("#player#", s)).collect(Collectors.toList());
                                        cmd.forEach(c -> MiningContest.getMiningContest().getServer().dispatchCommand(Bukkit.getConsoleSender(), c));
                                    }
                                }
                            }
                        }
                    });
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        PlayerData.points.put(p.getName(), 0);
                    }
                    data.put("start", false);
                }
                counter--;
                if (counter <= 10) {
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        p.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_countdown")).replaceAll("#giay#", String.valueOf(counter))));
                    }
                }
                if (counter <= 0) {
                    cancel();
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_end")));
                            }
                        }.runTask(MiningContest.getMiningContest());
                    }
                    Map<String, Integer> sortedMap = PlayerData.points.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    sortedMap.forEach((s, integer) -> {
                        if (integer >= FileManager.getConfig().getInt("limit_blocks")) {
                            Player p = Bukkit.getPlayer(s);
                            if (p != null) {
                                p.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer))));
                                List<String> player = new ArrayList<>(sortedMap.keySet());
                                boolean ontop = false;
                                for (int i = 0; i < 3; i++) {
                                    if (player.get(i).equalsIgnoreCase(s)) {
                                        ontop = true;
                                        List<String> cmd = FileManager.getConfig().getStringList("reward.top.top" + (i + 1)).stream().map(s1 -> s1.replace("#player#", s)).collect(Collectors.toList());
                                        cmd.forEach(c -> MiningContest.getMiningContest().getServer().dispatchCommand(Bukkit.getConsoleSender(), c));
                                    }
                                }
                                if (!ontop) {
                                    if (Bukkit.getPlayer(s) != null) {
                                        List<String> cmd = FileManager.getConfig().getStringList("reward.top.others").stream().map(s1 -> s1.replace("#player#", s)).collect(Collectors.toList());
                                        cmd.forEach(c -> MiningContest.getMiningContest().getServer().dispatchCommand(Bukkit.getConsoleSender(), c));
                                    }
                                }
                            }
                        }
                    });
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        PlayerData.points.put(p.getName(), 0);
                    }
                    data.put("start", false);
                }
            }
        }).runTaskTimer(MiningContest.getMiningContest(), 20L, 20L);
    }
}
