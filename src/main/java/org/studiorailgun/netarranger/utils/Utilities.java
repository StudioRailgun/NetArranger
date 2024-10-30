package org.studiorailgun.netarranger.utils;

import com.google.gson.Gson;

import org.studiorailgun.netarranger.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Utilities used by the tool
 */
public class Utilities {
    
    /**
     * Number of attempts to read before failing the function
     */
    static final int maxReadFails = 3;

    /**
     * The time to wait after a read failure
     */
    static final int READ_TIMEOUT_DURATION = 5;

    /**
     * Reads an input stream as a string
     * @param resourceInputStream The input stream
     * @return The string
     */
    public static String readBakedResourceToString(InputStream resourceInputStream){
        String rVal = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(resourceInputStream));
            int failCounter = 0;
            boolean reading = true;
            StringBuilder builder = new StringBuilder("");
            while(reading){
                if(reader.ready()){
                    failCounter = 0;
                    int nextValue = reader.read();
                    if(nextValue == -1){
                        reading = false;
                    } else {
                        builder.append((char)nextValue);
                    }
                } else {
                    failCounter++;
                    if(failCounter > maxReadFails){
                        reading = false;
                    } else {
                        try {
                            TimeUnit.MILLISECONDS.sleep(READ_TIMEOUT_DURATION);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            rVal = builder.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return rVal;
    }
    
    
    /**
     * Loads an object from a file baked into the jar
     * @param <T> The type of the object
     * @param fileName The name of the file
     * @param className The classname of the object
     * @return The object
     */
    public static <T>T loadObjectFromBakedJsonFile(String fileName, Class<T> className){
        T rVal = null;
        String rawJSON = Utilities.readBakedResourceToString(Main.class.getResourceAsStream(fileName));
        Gson gson = new Gson();
        rVal = gson.fromJson(rawJSON, className);
        return rVal;
    }
    
    
}
