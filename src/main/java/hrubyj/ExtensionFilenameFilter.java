package hrubyj;

import java.io.File;
import java.io.FilenameFilter;

public class ExtensionFilenameFilter implements FilenameFilter {

    private final String extension;

    public ExtensionFilenameFilter(String extension) {
        this.extension = extension.toLowerCase();
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(extension);
    }

}
