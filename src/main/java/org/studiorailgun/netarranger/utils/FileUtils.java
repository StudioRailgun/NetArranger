package org.studiorailgun.netarranger.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Writes a file
 */
public class FileUtils {
    
    /**
     * Writes bytes to a given file. Mainly wrapping the write function to prevent writing if the file contents are the same.
     * @param path The path to the file
     * @param bytes The bytes to write
     */
    public static void write(Path path, byte[] bytes){
        try {
            if(Files.exists(path)){
                String existingHash = FileUtils.getChecksum(Files.readAllBytes(path));
                String newHash = FileUtils.getChecksum(bytes);
                if(!existingHash.equals(newHash)){
                    Files.write(path,bytes);
                }
            } else {
                Files.write(path,bytes);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the checksum of an object
     * @param object The object
     * @return The checksum
     * @throws IOException Thrown on io errors reading the file
     * @throws NoSuchAlgorithmException Thrown if MD5 isn't supported
     */
    public static String getChecksum(Serializable object) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(baos.toByteArray());
            StringBuffer builder = new StringBuffer();
            for(byte byteCurr : bytes){
                builder.append(String.format("%02x",byteCurr));
            }
            return builder.toString();
        } finally {
            oos.close();
            baos.close();
        }
    }

}
