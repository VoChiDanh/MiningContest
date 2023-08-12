package net.danh.miningcontest.CMD;

import net.danh.miningcontest.API.CMDBase;
import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

import static net.danh.miningcontest.Contest.Mining.data;

public class Command extends CMDBase {
    public Command() {
        super("MiningContest");
    }

    @Override
    public void execute(CommandSender c, String[] args) {
        if (args.length == 1) {
            if (c.hasPermission("mc.admin")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    FileManager.getFileSetting().reload("config.yml");
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
                    sortedMap.forEach((s, integer) -> c.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer)))));
                    c.sendMessage(ChatManager.colorize("&7"));
                    c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_warning"))
                            .replace("#block#", String.valueOf(FileManager.getConfig().getInt("limit_blocks"))));
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
            commands.add("top");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}