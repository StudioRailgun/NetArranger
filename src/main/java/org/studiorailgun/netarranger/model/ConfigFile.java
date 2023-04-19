package org.studiorailgun.netarranger.model;

import java.util.List;

public class ConfigFile {
    List<Category> categories;

    String outputPath;
    
    String packageName;

    List<String> subfiles;

    public List<Category> getCategories() {
        return categories;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<String> getSubfiles(){
        return subfiles;
    }
    
    
}
