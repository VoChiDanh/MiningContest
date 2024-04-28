package net.danh.miningcontest.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.danh.miningcontest.Data.PlayerData;
import net.danh.miningcontest.Manager.ChatManager;
import net.danh.miningcontest.Manager.FileManager;
import net.danh.miningcontest.Manager.NumberManager;
import net.danh.miningcontest.MiningContest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
        if (args.equalsIgnoreCase("time"))
            return PlayerData.time();
        if (args.startsWith("top_")) {
            int top_number = NumberManager.getInteger(args.substring(4));
            AtomicReference<String> atomicReference = new AtomicReference<>("");
            Map<String, Integer> sortedMap = PlayerData.points.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            List<String> player = new ArrayList<>(sortedMap.keySet());
            sortedMap.forEach((s, integer) -> {
                for (int i = 0; i < player.size(); i++) {
                    if (i == (top_number - 1)) {
                        if (player.get(i) != null) {
                            if (player.get(i).equalsIgnoreCase(s)) {
                                atomicReference.set(ChatManager.colorizewp(Objects.requireNonNull(FileManager.getConfig().getString("message.contest_top")).replace("#name#", s).replace("#block#", String.valueOf(integer))));
                            }
                        }
                    }
                }
            });
            return atomicReference.get();
        }
        if (p == null) return null;
        if (args.equalsIgnoreCase("points"))
            return String.valueOf(PlayerData.getMinePoints(p));
        return null;
    }
}
