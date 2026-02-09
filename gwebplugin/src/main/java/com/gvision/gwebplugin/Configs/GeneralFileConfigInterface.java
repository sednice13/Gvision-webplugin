package com.gvision.gwebplugin.Configs;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public interface GeneralFileConfigInterface {


    void reloadFile();

    void saveFile();

    boolean checkFileExists(File file);

    FileConfiguration load(String fileName);

    void setString(String veriable, String value);

    void setBoolean(String veriable, boolean value);

    void setInt(String veriable, int value);

    void addStringtoArray(String veriable, String value);

    List<String> getStringList(String veriable);

    void removeStringFromArray(String veriable, String value);



   
}
