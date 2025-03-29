package org.studiorailgun.netarranger.classes;

import org.studiorailgun.netarranger.Main;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.utils.Utilities;

/**
 * This handles the conversion of socket bytes to a LinkedList of Bytes
 */
public class NetworkParser extends SourceGenerator {

    /**
     * Config file
     */
    ConfigFile config;
    
    /**
     * Constructor
     * @param config The config file
     */
    public NetworkParser(ConfigFile config){
        this.config = config;
    }
    
    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.raw;\n\n";
        
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".net.message.MessagePool;\n";
        fullFile = fullFile + "import " + config.getPackageName() + ".net.message.NetworkMessage;\n";
        
        //content
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/NetworkParser.txt"));
        
        return fullFile;
    }
    
}
