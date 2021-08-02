package cz.zcu.kiv.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main class
 */
public class Main {

    /**
     * Validates parameters.
     *
     * @param args parameters
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            File file = new File("since.txt");
            int since = 0;
            if (file.exists()) {
                System.out.println("File \"since.txt\" loaded.");
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    since = Integer.parseInt(bufferedReader.readLine());
                } catch (IOException e) {
                    System.out.println("Error reading \"since.txt\"! " + e);
                    return;
                }
            } else {
                System.out.println("File \"since.txt\" does not exists.");
            }
            new Analyzer().analyseGitHub(since, args[0]);
        } else if (args.length == 3) {
            try {
                File appFilesDir = new File(args[1]);
                File libFilesDir = new File(args[2]);
                if (appFilesDir.list().length > 0 && libFilesDir.list().length > 0) {
                    new Analyzer().analyseProject(args[0], appFilesDir, libFilesDir);
                }
            } catch (Exception e) {
                System.out.println("Files not found! Check the entered paths!");
            }
        } else {
            System.out.println("Wrong parameters!");
            System.out.println("To start analyzing project enter parameter <project_name> <path_to_appfiles> <path_to_libfiles>!");
            System.out.println("To start analyzing GitHub enter parameter <personal_access_token>!");
            System.out.println("How to generate personal access token -> https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token");
        }
    }
}
