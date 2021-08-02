package cz.zcu.kiv.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filters files according to their extension.
 */
public class ExtensionFilenameFilter implements FilenameFilter {

    /** file extension */
    private final String extension;

    public ExtensionFilenameFilter(String extension) {
        this.extension = extension.toLowerCase();
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(extension);
    }

}
