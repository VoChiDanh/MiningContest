package net.danh.miningcontest.CMD;

import net.danh.miningcontest.API.CMDBase;
import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.MiningContest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.danh.miningcontest.Contest.Mining.data;
import static net.danh.miningcontest.Contest.Mining.dataI;

public class Command extends CMDBase {
    public Command() {
        super("MiningContest");
    }

    private void checkTimes(CommandSender c) {
        String start = ChatManager.colorize(FileManager.getConfig().getString("message.contest_start_time"));
        String end = ChatManager.colorize(FileManager.getConfig().getString("message.contest_end_time"));
        if (data.get("start") && (dataI.get("end") > 0)) {
            c.sendMessage(end.replace("#time#", MiningContest.getTime(dataI.get("end"))));
        } else {
            c.sendMessage(start.replace("#time#", MiningContest.getTime(dataI.get("start"))));
        }
    }

    @Override
    public void execute(CommandSender c, String[] args) {
        if (args.length == 0) {
            checkTimes(c);
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                ChatManager.colorize(FileManager.getConfig().getStringList("help.user")).forEach(c::sendMessage);
                if (c.hasPermission("mc.admin")) {
                    ChatManager.colorize(FileManager.getConfig().getStringList("help.admin")).forEach(c::sendMessage);
                }
            }
            if (c.hasPermission("mc.admin")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    FileManager.getFileSetting().reload("config.yml");
                    c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.reload")));
                }
                if (args[0].equalsIgnoreCase("start")) {
                    if (!data.get("start")) {
                        Mining.StartMining();
                    } else {
                        c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_already_start")));
                    }
                }
                if (args[0].equalsIgnoreCase("end")) {
                    if (data.get("start")) {
                        Mining.setCancelled(true);
                    } else {
                        c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_not_start")));
                    }
                }
            }
            if (args[0].equalsIgnoreCase("top")) {
                if (data.get("start")) {
                    Map<String, Integer> sortedMap = PlayerData.points.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                    c.sendMessage(ChatManager.colorize("&7"));
                    List<String> player = new ArrayList<>(sortedMap.keySet());
                    sortedMap.forEach((s, integer) -> {
                        if (integer >= FileManager.getConfig().getInt("limit_blocks")) {
                            for (int i = 0; i < Math.min(player.size(), FileManager.getConfig().getInt("settings.contest_top_list")); i++) {
                                if (player.get(i) != null) {
                                    if (player.get(i).equalsIgnoreCase(s)) {
                                        c.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer))));
                                    }
                                }
                            }
                        }
                    });
                    if (c instanceof Player) {
                        AtomicBoolean ontop = new AtomicBoolean(false);
                        AtomicInteger top = new AtomicInteger(0);
                        AtomicInteger block = new AtomicInteger(0);
                        Player p = (Player) c;
                        sortedMap.forEach((s, integer) -> {
                            if (integer >= FileManager.getConfig().getInt("limit_blocks")) {
                                for (int i = 0; i < player.size(); i++) {
                                    if (player.get(i) != null) {
                                        if (player.get(i).equalsIgnoreCase(s) && s.equalsIgnoreCase(p.getName())) {
                                            ontop.set(true);
                                            top.set(i + 1);
                                            block.set(integer);
                                        }
                                    }
                                }
                            }
                        });
                        if (!ontop.get()) {
                            p.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_non_top")).replace("#block#", String.valueOf(FileManager.getConfig().getInt("limit_blocks")))));
                        } else {
                            p.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_self_top")).replace("#top#", String.valueOf(top.get())).replace("#block#", String.valueOf(block.get()))));
                        }
                        if (block.get() < FileManager.getConfig().getInt("limit_blocks")) {
                            c.sendMessage(ChatManager.colorize("&7"));
                            c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_warning")).replace("#block#", String.valueOf(FileManager.getConfig().getInt("limit_blocks"))));
                        }
                    }
                } else {
                    c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_not_start")));
                }
            }
        }
    }

    @Override
    public List<String> TabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("mc.admin")) {
                commands.add("reload");
                commands.add("start");
                commands.add("end");
            }
            commands.add("help");
            commands.add("top");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
