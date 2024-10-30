package org.studiorailgun.netarranger.model;

import java.util.List;

/**
 * <p>
 * The file that configures how the tool behaves
 * </p>
 * <p>
 * It can recursively define sub-config files. This is principally used for breaking the categories out into dedicated files.
 * </p>
 */
public class ConfigFile {

    /**
     * The categories within this config file
     */
    List<Category> categories;

    /**
     * The output path to generate to
     */
    String outputPath;
    
    /**
     * The name of the package at the output path
     */
    String packageName;

    /**
     * The sub config files that will be sourced after this one
     */
    List<String> subfiles;

    /**
     * Gets the categories within this config file
     * @return The categories within this config file
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Gets the output path to generate to
     * @return The output path to generate to
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Gets the name of the package at the output path
     * @return The name of the package at the output path
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the sub config files that will be sourced after this one
     * @return The sub config files that will be sourced after this one
     */
    public List<String> getSubfiles(){
        return subfiles;
    }
    
    
}
