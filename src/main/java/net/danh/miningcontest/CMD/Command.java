package net.danh.miningcontest.CMD;

import net.danh.miningcontest.API.CMDBase;
import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.Manager.NumberManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.danh.miningcontest.Contest.Mining.data;

public class Command extends CMDBase {
    public Command() {
        super("MiningContest");
    }

    @Override
    public void execute(CommandSender c, String[] args) {
        if (args.length == 0) {
            PlayerData.checkTimes(c);
        }
        if (args.length >= 3 && args.length <= 4) {
            if (c.hasPermission("mc.admin")) {
                Player p = Bukkit.getPlayer(args[1]);
                if (p != null) {
                    int amount = NumberManager.getInteger(args[2]);
                    if (amount > 0) {
                        if (data.get("start")) {
                            if (args[0].equalsIgnoreCase("add")) {
                                PlayerData.points.replace(p.getName(), PlayerData.points.get(p.getName()) + amount);
                                if (args[3].isEmpty() || !args[3].equalsIgnoreCase("s")) {
                                    c.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.add_points"))
                                            .replace("#points#", String.valueOf(amount))
                                            .replace("#player#", p.getName())));
                                }
                            }
                            if (args[0].equalsIgnoreCase("remove")) {
                                int new_int = PlayerData.points.get(p.getName()) - amount;
                                if (new_int >= 0) {
                                    PlayerData.points.replace(p.getName(), new_int);
                                    if (args[3].isEmpty() || !args[3].equalsIgnoreCase("s")) {
                                        c.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.remove_points"))
                                                .replace("#points#", String.valueOf(amount))
                                                .replace("#player#", p.getName())));
                                    }
                                }
                            }
                        } else {
                            c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_not_start")));
                        }
                    }
                }
            }
        }
        if (args.length == 2) {
            if (c.hasPermission("mc.admin")) {
                Player p = Bukkit.getPlayer(args[1]);
                if (p != null) {
                    if (args[0].equalsIgnoreCase("reset")) {
                        if (data.get("start")) {
                            PlayerData.points.replace(p.getName(), 0);
                            c.sendMessage(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.reset_points"))
                                    .replace("#player#", p.getName())));
                        } else {
                            c.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_not_start")));
                        }
                    }
                }
            }
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
                commands.add("add");
            }
            commands.add("help");
            commands.add("top");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        if (args.length == 2) {
            if (sender.hasPermission("mc.admin")) {
                if (args[0].equalsIgnoreCase("add")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        commands.add(player.getName());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        if (args.length == 3) {
            if (sender.hasPermission("mc.admin")) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()).contains(args[1])) {
                        commands.add("<number>");
                    }
                }
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
