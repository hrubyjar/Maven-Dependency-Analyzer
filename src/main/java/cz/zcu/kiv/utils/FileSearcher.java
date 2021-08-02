package cz.zcu.kiv.utils;

import java.io.File;
import java.util.*;

/**
 * Finds JAR files in projects.
 */
public class FileSearcher {

    /** project path */
    private String projectPath;

    /**
     * Constructor
     *
     * @param projectPath path to project
     */
    public FileSearcher(String projectPath){
        this.projectPath = projectPath;
    }

    /**
     * Finds all JAR files in all subdirectories "/target".
     *
     * @return file list
     */
    public List<File> findAppFiles() {
        List<File> appFiles = new ArrayList<>();
        Queue<File> directories = new LinkedList<>(Arrays.asList(new File(projectPath).listFiles()));
        while (!directories.isEmpty()) {
            File directory = directories.poll();
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && Arrays.stream(file.list()).filter(filename -> filename.equals("pom.xml")).toArray().length != 0
                            || file.isDirectory() && file.getName().equals("target")) {
                        directories.add(file);
                    } else if (file.getName().endsWith(".jar") && directory.getName().equals("target")) {
                        appFiles.add(file);
                    }
                }
            }
        }
        return appFiles;
    }

    /**
     * Finds all JAR files in all subdirectories "/target/dependency".
     *
     * @return file list
     */
    public List<File> findLibFiles() {
        Map<String, File> libFiles = new HashMap<>();
        Queue<File> directories = new LinkedList<>(Arrays.asList(new File(projectPath).listFiles()));
        while (!directories.isEmpty()) {
            File directory = directories.poll();
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if ((file.isDirectory() && Arrays.stream(file.list()).filter(filename -> filename.equals("pom.xml")).toArray().length != 0 &&
                            !file.getName().equals("dependency")) || (file.isDirectory() && file.getName().equals("target"))) {
                        directories.add(file);
                    } else if (file.getName().equals("dependency") && directory.getName().equals("target")) {
                        File[] dependencies = file.listFiles(new ExtensionFilenameFilter(".jar"));
                        if (dependencies != null) {
                            for (File dependency : dependencies) {
                                libFiles.put(dependency.getName(), dependency);
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(libFiles.values());
    }
}

