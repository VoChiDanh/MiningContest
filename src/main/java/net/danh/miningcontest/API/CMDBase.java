package net.danh.miningcontest.API;

import net.danh.miningcontest.MiningContest;
import org.bukkit.command.*;

import java.util.List;
import java.util.Objects;

public abstract class CMDBase implements CommandExecutor, TabCompleter {

    public CMDBase(String name) {
        PluginCommand pluginCommand = MiningContest.getMiningContest().getCommand(name);
        Objects.requireNonNull(pluginCommand).setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    public abstract void execute(CommandSender c, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return TabComplete(sender, args);
    }

    public abstract List<String> TabComplete(CommandSender sender, String[] args);
}
