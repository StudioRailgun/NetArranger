package org.studiorailgun.netarranger.classes;


import org.studiorailgun.netarranger.Main;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.utils.Utilities;

/**
 * Wrapper for Circular Byte Buffer datastructure
 */
public class CircularByteBuffer extends SourceGenerator {

    ConfigFile config;
    
    public CircularByteBuffer(ConfigFile config){
        this.config = config;
    }
    
    @Override
    public String generateClassSource() {
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.raw;\n\n";
        
        //content
        fullFile = fullFile + Utilities.readBakedResourceToString(Main.class.getResourceAsStream("/classTemplates/CircularByteBuffer.txt"));
        
        return fullFile;
    }
    
}
