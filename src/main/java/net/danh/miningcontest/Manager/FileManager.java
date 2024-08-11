package net.danh.miningcontest.Manager;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.danh.miningcontest.MiningContest;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class FileManager {

    private static final int config_version = 2;

    public static SimpleConfigurationManager getFileSetting() {
        return SimpleConfigurationManager.get();
    }

    public static FileConfiguration getConfig() {
        return getFileSetting().get("config.yml");
    }

    public static void loadFiles() {
        getFileSetting().build("", false, "config.yml");
    }

    public static void saveFiles() {
        getFileSetting().save("config.yml");
    }


    public static int getFileConfigVersion() {
        if (!getConfig().contains("version")) {
            return 0;
        } else if (getConfig().contains("version")) {
            return NumberManager.getInteger("version");
        }
        return 0;
    }

    public static int getPluginConfigVersion() {
        return config_version;
    }

    public static void updateConfig() {
        if (getPluginConfigVersion() > getFileConfigVersion()) {
            MiningContest.getMiningContest().getLogger().log(Level.WARNING, "Your config is updating...");
            getFileSetting().save("config.yml");
            File configFile = new File(MiningContest.getMiningContest().getDataFolder(), "config.yml");
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(MiningContest.getMiningContest().getResource("config.yml")), StandardCharsets.UTF_8));
            FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile);
            List<String> default_admin_help = defaultConfig.getStringList("help.admin");
            List<String> default_user_help = defaultConfig.getStringList("help.user");
            List<String> current_admin_help = currentConfig.getStringList("help.admin");
            List<String> current_user_help = currentConfig.getStringList("help.user");
            List<String> current_blacklist_world = currentConfig.getStringList("blacklist_world");
            List<String> default_blacklist_world = defaultConfig.getStringList("blacklist_world");
            if (current_admin_help.isEmpty()) {
                getConfig().set("help.admin", default_admin_help);
                getFileSetting().save("config.yml");
            }
            if (current_blacklist_world.isEmpty()) {
                getConfig().set("blacklist_world", default_blacklist_world);
                getFileSetting().save("config.yml");
            }
            if (current_user_help.isEmpty()) {
                getConfig().set("help.user", default_user_help);
                getFileSetting().save("config.yml");
            }
            getConfig().set("version", getPluginConfigVersion());
            try {
                ConfigUpdater.update(MiningContest.getMiningContest(), "config.yml", configFile, "points", "reward.top", "times");
                MiningContest.getMiningContest().getLogger().log(Level.WARNING, "Your config have been updated successful");
            } catch (IOException e) {
                MiningContest.getMiningContest().getLogger().log(Level.WARNING, "Can not update config by it self, please backup and rename your config then restart to get newest config!!");
                e.printStackTrace();
            }
            getFileSetting().reload("config.yml");
        }
    }
}
