package net.danh.miningcontest.Data;

import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.MiningContest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static net.danh.miningcontest.Contest.Mining.data;
import static net.danh.miningcontest.Contest.Mining.dataI;

public class PlayerData {
    public static HashMap<String, Integer> points = new HashMap<>();

    public static void checkTimes(CommandSender c) {
        c.sendMessage(time());
    }

    public static String time() {
        String start = ChatManager.colorizewp(FileManager.getConfig().getString("message.contest_start_time"));
        String end = ChatManager.colorizewp(FileManager.getConfig().getString("message.contest_end_time"));
        if (data.get("start") && (dataI.get("end") > 0)) {
            return ChatManager.colorizewp(end.replace("#time#", MiningContest.getTime(dataI.get("end"))));
        } else {
            return ChatManager.colorizewp(start.replace("#time#", MiningContest.getTime(dataI.get("start"))));
        }
    }

    public static int getMinePoints(Player p) {
        if (Mining.data.get("start")) {
            if (PlayerData.points.containsKey(p.getName()))
                return points.getOrDefault(p.getName(), 0);
            else return 0;
        }
        return 0;
    }

    public static void addMinePoints(Player p, String block_type) {
        if (Mining.data.get("start")) {
            if (PlayerData.points.containsKey(p.getName())) {
                int points;
                if (FileManager.getConfig().contains("points." + block_type)) {
                    points = FileManager.getConfig().getInt("points." + block_type);
                } else {
                    points = 0;
                }
                PlayerData.points.replace(p.getName(), PlayerData.points.get(p.getName()) + points);
            } else {
                PlayerData.points.put(p.getName(), 0);
            }
        }
    }
}
