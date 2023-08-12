package net.danh.miningcontest.Listener;

import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (Mining.data.get("start")) {
            if (PlayerData.points.containsKey(p.getName())) {
                int points;
                if (FileManager.getConfig().contains("points." + e.getBlock().getType().name())) {
                    points = FileManager.getConfig().getInt("points." + e.getBlock().getType().name());
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
