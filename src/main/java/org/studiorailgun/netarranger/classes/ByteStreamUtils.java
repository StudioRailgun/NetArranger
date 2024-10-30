package org.studiorailgun.netarranger.classes;

import org.studiorailgun.netarranger.Main;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.utils.Utilities;

/**
 * This contains utilities used to handle byte queues
 */
public class ByteStreamUtils extends SourceGenerator {

    /**
     * The config file
     */
    ConfigFile config;
    
    /**
     * Constructor
     * @param config The config file
     */
    public ByteStreamUtils(ConfigFile config){
        this.config = config;
    }
    
    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".util;\n\n";

        //add import for circular byte buffer
        fullFile = fullFile + "import io.github.studiorailgun.CircularByteBuffer;\n\n";
        
        //content
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/ByteStreamUtils.txt"));
        
        return fullFile;
    }
    
}
