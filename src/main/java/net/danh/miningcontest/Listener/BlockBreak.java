package net.danh.miningcontest.Listener;

import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.FileManager;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class BlockBreak implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        World world = e.getPlayer().getLocation().getWorld();
        if (world != null) {
            if (!FileManager.getConfig().getStringList("blacklist_world").contains(world.getName())) {
                if (!isPlacedBlock(e.getBlock())) {
                    PlayerData.addMinePoints(e.getPlayer(), e.getBlock().getType().name());
                }
            }
        }
    }

    public boolean isPlacedBlock(Block b) {
        List<MetadataValue> metaDataValues = b.getMetadata("PlacedBlock");
        for (MetadataValue value : metaDataValues) {
            return value.asBoolean();
        }
        return false;
    }
}
