package net.danh.miningcontest.Listener;

import net.danh.miningcontest.Data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        PlayerData.addMinePoints(e.getPlayer(), e.getBlock().getType().name());
    }
}
