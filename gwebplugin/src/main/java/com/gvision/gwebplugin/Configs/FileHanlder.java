package com.gvision.gwebplugin.Configs;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FileHanlder implements GeneralFileConfigInterface {

    private final JavaPlugin plugin;
    private final String fileName;
    private File file;
    private FileConfiguration config;

    public FileHanlder(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.config = load(fileName);
        
    }

    @Override
    public FileConfiguration load(String fileName) {
      
         File file = new File(plugin.getDataFolder(), fileName);

         System.out.println("Laddar fil: " + file.getAbsolutePath());
         

        if (!file.exists()) {
        

            plugin.saveResource(fileName, false); // kopierar från resources
        }
        config = YamlConfiguration.loadConfiguration(file);
        return config;

    }

    @Override
    public void reloadFile() {
        config = load(fileName);
    }

    @Override
    public void saveFile() {
        try {
            config.save(new File(plugin.getDataFolder(), fileName));
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Kunde inte spara konfigurationsfilen: " + fileName, ex);
        }
    }

    @Override
    public boolean checkFileExists() {
        File file = new File(plugin.getDataFolder(), fileName);
        return file.exists();
    }

}