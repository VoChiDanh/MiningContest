package net.danh.miningcontest.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.MiningContest;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MC_PAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "miningcontest";
    }

    @Override
    public @NotNull String getAuthor() {
        return MiningContest.getMiningContest().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return MiningContest.getMiningContest().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player p, @NotNull String args) {
        if (p == null) return null;
        if (args.equalsIgnoreCase("points")) {
            return String.valueOf(PlayerData.getMinePoints(p));
        }
        return null;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer p, @NotNull String args) {
        if (args.equalsIgnoreCase("time"))
            return PlayerData.time();
        return null;
    }
}
