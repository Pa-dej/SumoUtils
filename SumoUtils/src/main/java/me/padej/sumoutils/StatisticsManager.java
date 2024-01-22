package me.padej.sumoutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class StatisticsManager {
    private final File configFile;
    private final FileConfiguration config;

    public StatisticsManager(File dataFolder) {
        configFile = new File(dataFolder, "Statistics.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void updateStatistics(String playerName, String combination) {
        String key = playerName + "." + combination;
        int currentCount = config.getInt(key, 0);
        config.set(key, currentCount + 1);

        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}