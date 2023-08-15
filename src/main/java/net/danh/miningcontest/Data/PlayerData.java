package net.danh.miningcontest.Data;

import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Manager.FileManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerData {
    public static HashMap<String, Integer> points = new HashMap<>();

    public static void addMinePoints(Player p, String block_type) {
        if (Mining.data.get("start")) {
            if (PlayerData.points.containsKey(p.getName())) {
                int points;
                if (FileManager.getConfig().contains("points." + block_type)) {
                    points = FileManager.getConfig().getInt("points." + block_type);
                } else {
                    points = 1;
                }
                PlayerData.points.replace(p.getName(), PlayerData.points.get(p.getName()) + points);
            } else {
                PlayerData.points.put(p.getName(), 0);
            }
        }
    }
}
