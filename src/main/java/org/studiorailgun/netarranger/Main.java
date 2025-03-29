package org.studiorailgun.netarranger;

import com.google.gson.Gson;

import org.studiorailgun.netarranger.classes.ByteStreamUtils;
import org.studiorailgun.netarranger.classes.MessagePool;
import org.studiorailgun.netarranger.classes.NetworkMessage;
import org.studiorailgun.netarranger.classes.NetworkParser;
import org.studiorailgun.netarranger.classes.TypeBytes;
import org.studiorailgun.netarranger.classes.TypedMessage;
import org.studiorailgun.netarranger.model.Category;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.model.MessageType;
import org.studiorailgun.netarranger.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Main class
 */
public class Main {

    /**
     * Main method
     */
    public static void main(String args[]){
        //read in the config file
        File f = new File("./template.json");
        Gson gson = new Gson();
        try {
            ConfigFile config = gson.fromJson(Files.newBufferedReader(f.toPath()), ConfigFile.class);
            //recurse down if applicable
            if(config.getSubfiles() != null){
                for(String path : config.getSubfiles()){
                    Main.recursivelyParseConfigFiles(config, path);
                }
            }
            //log the categories, packets, and packet contents found
            for(Category cat : config.getCategories()){
                for(MessageType msg : cat.getMessageTypes()){
                    for(String data : msg.getData()){
                        System.out.println(cat.getCategoryName() + " - " + msg.getMessageName() + " - " + data);
                    }
                }
            }
            
            //delete the sources dir if it exists
            // recursiveDeletePath(config.getOutputPath());
            
            //write source files out
            Main.writeByteStreamUtils(config);
            Main.writeTypeBytesClass(config);
            Main.writeMessagePoolClass(config);
            Main.writeNetworkParserClass(config);
            Main.writeNetworkMessageClass(config);
            for(Category cat : config.getCategories()){
                Main.createMessageClassForCategory(config,cat);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Recursively parse config files
     * @param topLevelConfig The top level config file
     * @param currentPath The current path
     */
    static void recursivelyParseConfigFiles(ConfigFile topLevelConfig, String currentPath){
        File f = new File(currentPath);
        Gson gson = new Gson();
        try {
            //copy all categories to the top level one
            ConfigFile currentConfig = gson.fromJson(Files.newBufferedReader(f.toPath()), ConfigFile.class);
            if(currentConfig != null){
                for(Category category : currentConfig.getCategories()){
                    topLevelConfig.getCategories().add(category);
                }
                //recurse down if applicable
                if(currentConfig.getSubfiles() != null){
                    for(String path : currentConfig.getSubfiles()){
                        Main.recursivelyParseConfigFiles(topLevelConfig, path);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Recursively deletes directories and their contents at a given path
     * @param path The path
     */
    static void recursiveDeletePath(String path){
        File f = new File(path);
        if(f.isDirectory()){
            for(String child : f.list()){
                Main.recursiveDeletePath(path + "/" + child);
            }
            try {
                Files.delete(f.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Files.delete(f.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Writes the ByteStreamUtils.java file
     * @param config The config for the current run
     */
    static void writeByteStreamUtils(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/util/";
        String fullOutputPath = fullOutputDirectory + "ByteStreamUtils.java";
        
        ByteStreamUtils sourceGenerator = new ByteStreamUtils(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            FileUtils.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Writes the TypeBytes.java file
     * @param config The config for the current run
     */
    static void writeTypeBytesClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "TypeBytes.java";
        
        TypeBytes sourceGenerator = new TypeBytes(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            FileUtils.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Writes the MessagePool.java file
     * @param config The config for the current run
     */
    static void writeMessagePoolClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "MessagePool.java";
        
        MessagePool sourceGenerator = new MessagePool(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            FileUtils.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Writes the NetworkParser.java file
     * @param config The config for the current run
     */
    static void writeNetworkParserClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/raw/";
        String fullOutputPath = fullOutputDirectory + "NetworkParser.java";
        
        NetworkParser sourceGenerator = new NetworkParser(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            FileUtils.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Writes the NetworkMessage.java file
     * @param config The config for the current run
     */
    static void writeNetworkMessageClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "NetworkMessage.java";
        
        NetworkMessage sourceGenerator = new NetworkMessage(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            FileUtils.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Writes the <Category>Message.java file
     * @param config The config for the current run
     */
    static void createMessageClassForCategory(ConfigFile config, Category cat){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "" + cat.getCategoryName() + "Message.java";
        
        TypedMessage sourceGenerator = new TypedMessage(config,cat);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            FileUtils.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
