package org.studiorailgun.netarranger.classes;

import org.studiorailgun.netarranger.Main;
import org.studiorailgun.netarranger.model.Category;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.utils.Utilities;

public class MessagePool  extends SourceGenerator {
    
    ConfigFile config;
    
    public MessagePool(ConfigFile config){
        this.config = config;
    }

    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.message;\n\n";
        
        //intro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/MessagePoolFirstPart.txt"));
        
        //add type bytes for categories
        for(Category cat : config.getCategories()){
            fullFile = fullFile + "    /**\n     * Pools " + cat.getCategoryName() + " messages\n     */\n";
            fullFile = fullFile + "    List<NetworkMessage> " + cat.getCategoryName().toLowerCase() + "MessagePool = new LinkedList<NetworkMessage>();\n";
            fullFile = fullFile + "\n";
        }

        //part two
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/MessagePoolSecondPart.txt"));


        //get method
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * Gets a network message from the pool. Allocates if no free one is available.\n";
        fullFile = fullFile + "     * @param type The type of the message\n";
        fullFile = fullFile + "     * @return A network message of the requested type\n";
        fullFile = fullFile + "     */\n";
        fullFile = fullFile + "    public NetworkMessage get(MessageType type){\n";
        fullFile = fullFile + "        NetworkMessage rVal = null;\n";
        fullFile = fullFile + "        lock.lock();\n";
        int incrementer = 0;
        for(Category cat : config.getCategories()){
            if(incrementer == 0){
                fullFile = fullFile + "        if(type == MessageType." + cat.getCategoryName().toUpperCase() + "_MESSAGE){\n";
            } else {
                fullFile = fullFile + "        } else if(type == MessageType." + cat.getCategoryName().toUpperCase() + "_MESSAGE){\n";
            }
            fullFile = fullFile + "            if(!alwaysAllocate && " + cat.getCategoryName().toLowerCase() + "MessagePool.size() > 0){\n";
            fullFile = fullFile + "                rVal = " + cat.getCategoryName().toLowerCase() + "MessagePool.remove(0);\n";
            fullFile = fullFile + "            } else {\n";
            fullFile = fullFile + "                rVal = new " + cat.getCategoryName() + "Message();\n";
            fullFile = fullFile + "            }\n";
            incrementer++;
        }
        fullFile = fullFile + "        } else {\n";
        fullFile = fullFile + "            throw new Error(\"Unsupported message type! \" + type);\n";
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "        lock.unlock();\n";
        fullFile = fullFile + "        return rVal;\n";
        fullFile = fullFile + "    }\n";
        fullFile = fullFile + "\n";


        //release method
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * Releases a message back into the pool\n";
        fullFile = fullFile + "     * @param message The message\n";
        fullFile = fullFile + "     */\n";
        fullFile = fullFile + "    public void release(NetworkMessage message){\n";
        fullFile = fullFile + "        lock.lock();\n";
        incrementer = 0;
        for(Category cat : config.getCategories()){
            if(incrementer == 0){
                fullFile = fullFile + "        if(message instanceof " + cat.getCategoryName() + "Message){\n";
            } else {
                fullFile = fullFile + "        } else if(message instanceof " + cat.getCategoryName() + "Message){\n";
            }
            fullFile = fullFile + "            if(" + cat.getCategoryName().toLowerCase() + "MessagePool.size() < 1000){\n";
            fullFile = fullFile + "                " + cat.getCategoryName().toLowerCase() + "MessagePool.add(message);\n";
            fullFile = fullFile + "            }\n";
            incrementer++;
        }
        fullFile = fullFile + "        } else {\n";
        fullFile = fullFile + "            throw new Error(\"Unsupported message type! \" + message.getClass());\n";
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "        lock.unlock();\n";
        fullFile = fullFile + "    }\n";
        fullFile = fullFile + "\n";

        
        //outro of file
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/MessagePoolThirdPart.txt"));
        
        //return
        return fullFile;
    }
    
}