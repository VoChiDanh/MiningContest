package net.danh.miningcontest.Listener;

import dev.lone.itemsadder.api.CustomBlock;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.MiningContest;
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
                    if (!isIABlock(e.getBlock()))
                        PlayerData.addMinePoints(e.getPlayer(), e.getBlock().getType().name());
                    else {
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(e.getBlock());
                        if (customBlock != null) {
                            PlayerData.addMinePoints(e.getPlayer(), customBlock.getId());
                        }
                    }
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

    public boolean isIABlock(Block b) {
        return MiningContest.getMiningContest().getServer().getPluginManager().getPlugin("ItemsAdder") != null
                && CustomBlock.byAlreadyPlaced(b) != null;
    }
}