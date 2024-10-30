package org.studiorailgun.netarranger.classes;

/**
 * Represents a source file that can be generated
 */
public abstract class SourceGenerator {
    
    
    /**
     * Gets the source code for a new class in String format
     * @return The string containing the source code for a new class in String format
     */
    public abstract String generateClassSource();
    
    
}
