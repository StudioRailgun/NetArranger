package org.studiorailgun.netarranger.model;

import java.util.List;

/**
 * A type of message that can be handled by the parser
 */
public class MessageType {

    /**
     * The name of the message type
     */
    private String messageName;

    /**
     * The list of variables contained in the message
     */
    private List<String> data;

    /**
     * If true, the parser will use a custom, user provided function to parse this message type from the byte stream
     */
    private Boolean customParser;

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

    /**
     * Gets whether to use a custom parser or not
     * @return true to use a custom parser, false otherwise
     */
    public Boolean getCustomParser(){
        return customParser;
    }
    
}
