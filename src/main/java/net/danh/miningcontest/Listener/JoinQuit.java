package net.danh.miningcontest.Listener;

import net.danh.miningcontest.Contest.Mining;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class JoinQuit implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Mining.data.get("start")) {
            if (!PlayerData.points.containsKey(p.getName())) {
                PlayerData.points.put(p.getName(), 0);
                p.sendMessage(ChatManager.colorize(FileManager.getConfig().getString("message.contest_pre_join")));
            }
        } else {
            if (PlayerData.points.containsKey(p.getName())) {
                PlayerData.points.put(p.getName(), 0);
            }
        }
        if (Mining.bossBar != null) {
            Mining.bossBar.addPlayer(p);
        }
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Mining.bossBar != null) {
            Mining.bossBar.removePlayer(p);
        }
    }
}
