package org.studiorailgun.netarranger.model;

/**
 * A type of data that can be transported in a packet
 */
public class Data {

    /**
     * The name of the variable
     */
    String name;

    /**
     * The type of the variable
     */
    String type;

    /**
     * An optional description of this variable
     */
    String description;

    /**
     * Gets the name of the variable
     * @return The name of the variable
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of the variable
     * @return The type of the variable
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the description of this variable
     * @return The description
     */
    public String getDescription(){
        return description;
    }
    
    
}
