package org.studiorailgun.netarranger.classes;

import org.studiorailgun.netarranger.Main;
import org.studiorailgun.netarranger.model.Category;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.model.MessageType;
import org.studiorailgun.netarranger.utils.Utilities;

/**
 * This is a generic message type that is used in parsing specific message types
 */
public class NetworkMessage extends SourceGenerator {

    /**
     * Config file
     */
    ConfigFile config;
    
    /**
     * Constructor
     * @param config The config file
     */
    public NetworkMessage(ConfigFile config){
        this.config = config;
    }
    
    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.message;\n\n";
        
        //intro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkMessageFirstPart.txt"));
        
        //construct enum
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "        " + cat.getCategoryName().toUpperCase() + "_MESSAGE,\n";
        }
        
        //second part of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkMessageSecondPart.txt"));
        
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "                case TypeBytes.MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + ":\n";
            fullFile = fullFile + "                    secondByte = byteBuffer.get();\n";
            fullFile = fullFile + "                    switch(secondByte){\n";
            for(MessageType type : cat.getMessageTypes()){
                fullFile = fullFile + "                    case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
                // fullFile = fullFile + "                        if(" + cat.getCategoryName() + "Message.canParseMessage(byteBuffer,secondByte)){\n";
                fullFile = fullFile + "                        rVal = " + cat.getCategoryName() + "Message.parse" + type.getMessageName() + "Message(byteBuffer,pool,customParserMap);\n";
                // fullFile = fullFile + "                        }\n";
                fullFile = fullFile + "                        break;\n";
            }
            fullFile = fullFile + "                }\n";
            fullFile = fullFile + "                break;\n";
        }

        //error checking
        fullFile = fullFile + "                default:\n";
        fullFile = fullFile + "                throw new Error(\"Unsupported message type! \" + firstByte);\n";
        
        //third part of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkMessageThirdPart.txt"));
        
        return fullFile;
    }
    
}
