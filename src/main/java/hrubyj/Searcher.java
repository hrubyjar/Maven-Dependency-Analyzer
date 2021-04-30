package hrubyj;

import java.io.File;
import java.util.*;

public class Searcher {

    private String projectPath;

    public Searcher(String projectPath){
        this.projectPath = projectPath;
    }

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
                    } else if (file.getName().startsWith("original-") && file.getName().endsWith(".jar") && directory.getName().equals("target")) {
                        appFiles.add(file);
                    }
                }
            }
        }
        return appFiles;
    }

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

