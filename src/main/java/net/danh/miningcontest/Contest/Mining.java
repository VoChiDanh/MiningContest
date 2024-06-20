package net.danh.miningcontest.Contest;

import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.ColorManager;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.Manager.NumberManager;
import net.danh.miningcontest.MiningContest;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Mining {

    public static HashMap<String, Boolean> data = new HashMap<>();
    public static HashMap<String, Integer> dataI = new HashMap<>();
    public static BossBar bossBar;


    public static void setCancelled(boolean cancelled) {
        data.replace("end", cancelled);
    }

    public static void createBossBar() {
        if (FileManager.getConfig().getBoolean("bossbar.enable")) {
            String title = ColorManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("bossbar.title"))
                    .replace("#second#", String.valueOf(dataI.get("end"))));
            BarColor barColor = BarColor.valueOf(Objects.requireNonNull(FileManager.getConfig().getString("bossbar.bar_color")).toUpperCase());
            BarStyle barStyle = BarStyle.valueOf(Objects.requireNonNull(FileManager.getConfig().getString("bossbar.bar_style")).toUpperCase());
            bossBar = Bukkit.createBossBar(title, barColor, barStyle);
            bossBar.setVisible(true);
            bossBar.setProgress((double) dataI.get("end") / (double) FileManager.getConfig().getInt("settings.contest_times"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                addPlayer(p);
            }
        }
    }

    public static void addPlayer(Player p) {
        bossBar.addPlayer(p);
    }

    public static void removePlayer(Player p) {
        bossBar.removePlayer(p);
    }

    public static BossBar getBossBar() {
        return bossBar;
    }

    public static double getPercentages(int current, int max) {
        int percent = current * 100 / max;
        return Math.round(percent * 10.0) / 10.0;
    }


    public static void StartMining() {
        data.put("start", true);
        Bukkit.broadcastMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_start")));
        Bukkit.broadcastMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_warning"))
                .replace("#block#", String.valueOf(FileManager.getConfig().getInt("limit_blocks"))));
        for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
            PlayerData.points.put(p.getName(), 0);
        }
        dataI.replace("end", FileManager.getConfig().getInt("settings.contest_times"));
        createBossBar();
        (new BukkitRunnable() {
            int counter = FileManager.getConfig().getInt("settings.contest_times");

            public void run() {
                if (data.get("end")) {
                    cancel();
                    if (FileManager.getConfig().getBoolean("bossbar.enable") && bossBar != null) {
                        double percent = (double) dataI.get("end") / (double) FileManager.getConfig().getInt("settings.contest_times");
                        bossBar.setProgress(percent);
                        for (String percentID : Objects.requireNonNull(FileManager.getConfig().getConfigurationSection("bossbar.bar_per_percent")).getKeys(false)) {
                            String rangePercent = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".percent");
                            if (rangePercent != null) {
                                String[] rpSplit = rangePercent.split("to");
                                double low = NumberManager.getDouble(rpSplit[0]);
                                double high = NumberManager.getDouble(rpSplit[1]);
                                if (percent >= low && percent <= high) {
                                    String rangeColor = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".color");
                                    String rangeTitle = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".title");
                                    String rangeStyle = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".style");
                                    if (rangeColor != null) {
                                        bossBar.setColor(BarColor.valueOf(rangeColor.toUpperCase()));
                                    }
                                    if (rangeTitle != null) {
                                        bossBar.setTitle(ColorManager.colorize(Objects.requireNonNull(rangeTitle)
                                                .replace("#second#", String.valueOf(dataI.get("end")))));
                                    }
                                    if (rangeStyle != null) {
                                        bossBar.setStyle(BarStyle.valueOf(rangeStyle.toUpperCase()));
                                    }
                                }
                            }
                        }
                        bossBar.removeAll();
                        bossBar = null;
                    }
                    data.put("end", false);
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_end")));
                            }
                        }.runTask(MiningContest.getMiningContest());
                    }
                    dataI.replace("end", 0);
                    Map<String, Integer> sortedMap = PlayerData.points.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    sortedMap.forEach((s, integer) -> {
                        if (integer >= FileManager.getConfig().getInt("limit_blocks")) {
                            Player p = Bukkit.getPlayer(s);
                            if (p != null) {
                                MiningContest.getMiningContest().getServer().broadcastMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer))));
                                List<String> player = new ArrayList<>(sortedMap.keySet());
                                boolean ontop = false;
                                for (int i = 0; i < Math.min(player.size(), FileManager.getConfig().getInt("settings.contest_top_list")); i++) {
                                    if (player.get(i) != null) {
                                        if (player.get(i).equalsIgnoreCase(s)) {
                                            ontop = true;
                                            List<String> cmd = FileManager.getConfig().getStringList("reward.top.top" + (i + 1)).stream().map(s1 -> s1.replace("#player#", s)).collect(Collectors.toList());
                                            cmd.forEach(c -> MiningContest.getMiningContest().getServer().dispatchCommand(Bukkit.getConsoleSender(), c));
                                        }
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
                    PlayerData.points.replaceAll((p, v) -> 0);
                    data.put("start", false);
                }
                counter--;
                if (FileManager.getConfig().getBoolean("bossbar.enable") && bossBar != null) {
                    double percent = (double) dataI.get("end") / (double) FileManager.getConfig().getInt("settings.contest_times");
                    bossBar.setProgress(percent);
                    for (String percentID : Objects.requireNonNull(FileManager.getConfig().getConfigurationSection("bossbar.bar_per_percent")).getKeys(false)) {
                        String rangePercent = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".percent");
                        if (rangePercent != null) {
                            String[] rpSplit = rangePercent.split("to");
                            double low = NumberManager.getDouble(rpSplit[0]);
                            double high = NumberManager.getDouble(rpSplit[1]);
                            if (percent >= low && percent <= high) {
                                String rangeColor = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".color");
                                String rangeTitle = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".title");
                                String rangeStyle = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".style");
                                if (rangeColor != null) {
                                    bossBar.setColor(BarColor.valueOf(rangeColor.toUpperCase()));
                                }
                                if (rangeTitle != null) {
                                    bossBar.setTitle(ColorManager.colorize(Objects.requireNonNull(rangeTitle)
                                            .replace("#second#", String.valueOf(dataI.get("end")))));
                                }
                                if (rangeStyle != null) {
                                    bossBar.setStyle(BarStyle.valueOf(rangeStyle.toUpperCase()));
                                }
                            }
                        }
                    }
                }
                if (counter <= 10) {
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        p.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_countdown")).replaceAll("#second#", String.valueOf(counter))));
                    }
                }
                if (counter <= 0) {
                    cancel();
                    if (FileManager.getConfig().getBoolean("bossbar.enable") && bossBar != null) {
                        double percent = (double) dataI.get("end") / (double) FileManager.getConfig().getInt("settings.contest_times");
                        bossBar.setProgress(percent);
                        for (String percentID : Objects.requireNonNull(FileManager.getConfig().getConfigurationSection("bossbar.bar_per_percent")).getKeys(false)) {
                            String rangePercent = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".percent");
                            if (rangePercent != null) {
                                String[] rpSplit = rangePercent.split("to");
                                double low = NumberManager.getDouble(rpSplit[0]);
                                double high = NumberManager.getDouble(rpSplit[1]);
                                if (percent >= low && percent <= high) {
                                    String rangeColor = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".color");
                                    String rangeTitle = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".title");
                                    String rangeStyle = FileManager.getConfig().getString("bossbar.bar_per_percent." + percentID + ".style");
                                    if (rangeColor != null) {
                                        bossBar.setColor(BarColor.valueOf(rangeColor.toUpperCase()));
                                    }
                                    if (rangeTitle != null) {
                                        bossBar.setTitle(ColorManager.colorize(Objects.requireNonNull(rangeTitle)
                                                .replace("#second#", String.valueOf(dataI.get("end")))));
                                    }
                                    if (rangeStyle != null) {
                                        bossBar.setStyle(BarStyle.valueOf(rangeStyle.toUpperCase()));
                                    }
                                }
                            }
                        }
                        bossBar.removeAll();
                        bossBar = null;
                    }
                    for (Player p : MiningContest.getMiningContest().getServer().getOnlinePlayers()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_end")));
                            }
                        }.runTask(MiningContest.getMiningContest());
                    }
                    dataI.replace("end", 0);
                    Map<String, Integer> sortedMap = PlayerData.points.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    sortedMap.forEach((s, integer) -> {
                        if (integer >= FileManager.getConfig().getInt("limit_blocks")) {
                            Player p = Bukkit.getPlayer(s);
                            if (p != null) {
                                MiningContest.getMiningContest().getServer().broadcastMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer))));
                                List<String> player = new ArrayList<>(sortedMap.keySet());
                                boolean ontop = false;
                                for (int i = 0; i < Math.min(player.size(), FileManager.getConfig().getInt("settings.contest_top_list")); i++) {
                                    if (player.get(i) != null) {
                                        if (player.get(i).equalsIgnoreCase(s)) {
                                            ontop = true;
                                            List<String> cmd = FileManager.getConfig().getStringList("reward.top.top" + (i + 1)).stream().map(s1 -> s1.replace("#player#", s)).collect(Collectors.toList());
                                            cmd.forEach(c -> MiningContest.getMiningContest().getServer().dispatchCommand(Bukkit.getConsoleSender(), c));
                                        }
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
                    PlayerData.points.replaceAll((p, v) -> 0);
                    data.put("start", false);
                }
            }
        }).runTaskTimer(MiningContest.getMiningContest(), 20L, 20L);
    }
}
