package org.studiorailgun.netarranger.model;

import java.util.List;

/**
 * A category that contains types of data and messages (packets) that transport that data
 */
public class Category {

    /**
     * The name of the category
     */
    String categoryName;

    /**
     * The types of messages contained in this category
     */
    List<MessageType> messageTypes;

    /**
     * The data definitions contained in this cateogry
     */
    List<Data> data;

    /**
     * Gets the types of messages contained in this category
     * @return The types of messages contained in this category
     */
    public List<MessageType> getMessageTypes() {
        return messageTypes;
    }

    /**
     * Gets the name of the category
     * @return The name of the category
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Gets the data definitions contained in this cateogry
     * @return The data definitions contained in this cateogry
     */
    public List<Data> getData() {
        return data;
    }
    
    
}
