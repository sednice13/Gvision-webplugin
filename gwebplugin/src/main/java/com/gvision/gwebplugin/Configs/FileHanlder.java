package com.gvision.gwebplugin.Configs;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

    /** 
     * @param fileName
     * @return FileConfiguration
     */
    @Override
    public FileConfiguration load(String fileName) {

        File file = new File(plugin.getDataFolder(), fileName);

        System.out.println("Laddar fil: " + file.getAbsolutePath());

        if (!checkFileExists(file)) {

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

    /** 
     * @return boolean
     */
    @Override
    public boolean checkFileExists(File file) {
       
        return file.exists();
    }

    /** 
     * Sets a string value in the configuration file.
     * @param veriable config key
     * @param value value String
     */
    @Override
    public void setString(String veriable, String value) {
        config.set(veriable, value);
        saveFile();

    }
    /** 
     * Sets a boolean value in the configuration file.
     * @param veriable config key
     * @param value value boolean
     */
    @Override
    public void setBoolean(String veriable, boolean value) {
        config.set(veriable, value);
        saveFile();
}

    /** 
     * Sets an integer value in the configuration file.
     * @param veriable
     * @param value
     */
    @Override
    public void setInt(String veriable, int value) {
        config.set(veriable, value);
        saveFile();
    }

    /** 
     * Adds a string to a list in the configuration file.
     * @param veriable config key
     * @param value value String to add
     */
    @Override
    public void addStringtoArray(String veriable, String value) {
        java.util.List<String> list = config.getStringList(veriable);
        if (!list.contains(value)) {
            list.add(value);
            config.set(veriable, list);
            saveFile(); 
        } 
    }

    /** 
     * Gets a list of strings from the configuration file.
     * @param veriable config key
     * @return List<String> list of strings
     */
    @Override
    public List<String> getStringList(String veriable) {
        return config.getStringList(veriable);
    }

   

}
