package org.studiorailgun.netarranger;

import com.google.gson.Gson;

import org.studiorailgun.netarranger.classes.ByteStreamUtils;
import org.studiorailgun.netarranger.classes.CircularByteBuffer;
import org.studiorailgun.netarranger.classes.NetworkMessage;
import org.studiorailgun.netarranger.classes.NetworkParser;
import org.studiorailgun.netarranger.classes.TypeBytes;
import org.studiorailgun.netarranger.classes.TypedMessage;
import org.studiorailgun.netarranger.model.Category;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.model.MessageType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    public static void main(String args[]){
        //read in the config file
        File f = new File("./template.json");
        Gson gson = new Gson();
        try {
            ConfigFile config = gson.fromJson(Files.newBufferedReader(f.toPath()), ConfigFile.class);
            //recurse down if applicable
            if(config.getSubfiles() != null){
                for(String path : config.getSubfiles()){
                    recursivelyParseConfigFiles(config, path);
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
            recursiveDeletePath(config.getOutputPath());
            
            //write source files out
            writeCircularByteBuffer(config);
            writeByteStreamUtils(config);
            writeTypeBytesClass(config);
            writeNetworkParserClass(config);
            writeNetworkMessageClass(config);
            for(Category cat : config.getCategories()){
                createMessageClassForCategory(config,cat);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //recursively parse config files
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
                        recursivelyParseConfigFiles(topLevelConfig, path);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //recursively deletes directories and their contents
    static void recursiveDeletePath(String path){
        File f = new File(path);
        if(f.isDirectory()){
            for(String child : f.list()){
                recursiveDeletePath(path + "/" + child);
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

    //write out CircularByteBuffer.java
    static void writeCircularByteBuffer(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/raw/";
        String fullOutputPath = fullOutputDirectory + "CircularByteBuffer.java";
        
        CircularByteBuffer sourceGenerator = new CircularByteBuffer(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //write out ByteStreamUtils.java
    static void writeByteStreamUtils(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/util/";
        String fullOutputPath = fullOutputDirectory + "ByteStreamUtils.java";
        
        ByteStreamUtils sourceGenerator = new ByteStreamUtils(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //write out TypeBytes.java
    static void writeTypeBytesClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "TypeBytes.java";
        
        TypeBytes sourceGenerator = new TypeBytes(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //write out NetworkParser.java
    static void writeNetworkParserClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/raw/";
        String fullOutputPath = fullOutputDirectory + "NetworkParser.java";
        
        NetworkParser sourceGenerator = new NetworkParser(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //write out NetworkMessage.java
    static void writeNetworkMessageClass(ConfigFile config){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "NetworkMessage.java";
        
        NetworkMessage sourceGenerator = new NetworkMessage(config);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //write out <Category>Message.java
    static void createMessageClassForCategory(ConfigFile config, Category cat){
        String fullOutputDirectory = config.getOutputPath() + "/net/message/";
        String fullOutputPath = fullOutputDirectory + "" + cat.getCategoryName() + "Message.java";
        
        TypedMessage sourceGenerator = new TypedMessage(config,cat);
        
        try {
            Files.createDirectories(new File(fullOutputDirectory).toPath());
            Files.write(new File(fullOutputPath).toPath(), sourceGenerator.generateClassSource().getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
