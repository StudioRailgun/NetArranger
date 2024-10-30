package org.studiorailgun.netarranger.model;

import java.util.List;

/**
 * A type of message that can be handled by the parser
 */
public class MessageType {

    /**
     * The name of the message type
     */
    String messageName;

    /**
     * The list of variables contained in the message
     */
    List<String> data;

    /**
     * Gets the list of variables contained in the message
     * @return The list of variables contained in the message
     */
    public List<String> getData() {
        return data;
    }

    /**
     * Gets the name of the message type
     * @return The name of the message type
     */
    public String getMessageName() {
        return messageName;
    }
    
}
