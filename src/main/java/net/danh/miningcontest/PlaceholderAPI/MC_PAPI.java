package net.danh.miningcontest.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.MiningContest;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.danh.miningcontest.Contest.Mining.data;

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
    public @Nullable String onRequest(OfflinePlayer p, @NotNull String args) {
        if (args.equalsIgnoreCase("time"))
            return PlayerData.time();
        if (args.equalsIgnoreCase("top")) {
            if (data.get("start")) {
                List<String> topString = new ArrayList<>();
                Map<String, Integer> sortedMap = PlayerData.points.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                topString.add(ChatManager.colorize("&7"));
                List<String> player = new ArrayList<>(sortedMap.keySet());
                sortedMap.forEach((s, integer) -> {
                    if (integer >= FileManager.getConfig().getInt("limit_blocks")) {
                        for (int i = 0; i < Math.min(player.size(), FileManager.getConfig().getInt("settings.contest_top_list")); i++) {
                            if (player.get(i) != null) {
                                if (player.get(i).equalsIgnoreCase(s)) {
                                    topString.add(ChatManager.colorize(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer))));
                                }
                            }
                        }
                    }
                });
                return topString.toString();
            } else return PlayerData.time();
        }
        return null;
    }
}
