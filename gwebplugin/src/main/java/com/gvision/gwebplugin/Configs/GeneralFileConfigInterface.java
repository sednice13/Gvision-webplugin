package com.gvision.gwebplugin.Configs;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

public interface GeneralFileConfigInterface {


    void reloadFile();

    void saveFile();

    boolean checkFileExists(File file);

    FileConfiguration load(String fileName);

    void setString(String veriable, String value);

    void setBoolean(String veriable, boolean value);

    void setInt(String veriable, int value);

   
}
