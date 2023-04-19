package org.studiorailgun.netarranger.model;

import java.util.List;

public class MessageType {
    String messageName;
    List<String> data;

    public List<String> getData() {
        return data;
    }

    public String getMessageName() {
        return messageName;
    }
    
}
