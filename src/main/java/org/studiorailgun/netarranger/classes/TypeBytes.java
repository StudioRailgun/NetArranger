package org.studiorailgun.netarranger.classes;

import java.util.HashMap;

import org.studiorailgun.netarranger.Main;
import org.studiorailgun.netarranger.model.Category;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.model.Data;
import org.studiorailgun.netarranger.model.MessageType;
import org.studiorailgun.netarranger.utils.Utilities;

/*

This contains constants that the other classes use to distinguish different message types/sizes

*/

public class TypeBytes extends SourceGenerator {
    
    ConfigFile config;
    
    public TypeBytes(ConfigFile config){
        this.config = config;
    }

    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.message;\n\n";
        
        //intro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/TypeBytesFirstPart.txt"));
        
        //add type bytes for categories
        fullFile = fullFile + "/*\nMessage categories\n*/\n";
        int incrementer = 0;
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "    public static final byte MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + " = " + incrementer + ";\n";
            incrementer++;
        }
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "    /*\n";
            fullFile = fullFile + "    " + cat.getCategoryName() + " subcategories\n";
            fullFile = fullFile + "    */\n";
            incrementer = 0;
            for(MessageType type : cat.getMessageTypes()){
                fullFile = fullFile + "    public static final byte " + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + " = " + incrementer + ";\n";
                incrementer++;
            }
            fullFile = fullFile + "    /*\n";
            fullFile = fullFile + "    " + cat.getCategoryName() + " packet sizes\n";
            fullFile = fullFile + "    */\n";
            for(MessageType type : cat.getMessageTypes()){
                //get all data types
                HashMap<String,String> typeMap = new HashMap<String,String>();
                for(Data variable : cat.getData()){
                    typeMap.put(variable.getName(), variable.getType());
                }

                int packetSize = 2; // 2 comes from 2 bytes for header
                boolean variableSize = false;
                for(String variable : type.getData()){
                    switch(typeMap.get(variable)){
                        case "FIXED_INT":
                            packetSize = packetSize + 4;
                            break;
                        case "FIXED_FLOAT":
                            packetSize = packetSize + 4;
                            break;
                        case "FIXED_LONG":
                            packetSize = packetSize + 8;
                            break;
                        case "VAR_STRING":
                            variableSize = true;
                            break;
                        case "FIXED_DOUBLE":
                            packetSize = packetSize + 8;
                            break;
                        case "BYTE_ARRAY":
                            variableSize = true;
                            break;
                    }
                }
                if(!variableSize){
                    if(packetSize < 127){
                        fullFile = fullFile + "    public static final byte " + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + "_SIZE = " + packetSize + ";\n";
                    } else {
                        fullFile = fullFile + "    public static final short " + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + "_SIZE = " + packetSize + ";\n";
                    }
                }
            }
        }
        
        //outro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/TypeBytesSecondPart.txt"));
        
        //return
        return fullFile;
    }
    
}
