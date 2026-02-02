package com.gvision.gwebplugin.Configs;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

public interface GeneralFileConfigInterface {


    void reloadFile();

    void saveFile();

    boolean checkFileExists();

    public FileConfiguration load(String fileName);


}
