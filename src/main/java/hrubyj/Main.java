package hrubyj;

import org.apache.maven.shared.invoker.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    // test url = https://github.com/hrubyjar/MavenRepo.git

    public static void main(String[] args) throws IOException, GitAPIException, MavenInvocationException {
        Scanner sc = new Scanner(System.in);
        System.out.println("URL: ");
        String url = sc.nextLine();
        File[] dependFiles = getDependJarFiles(url);
    }

    private static File[] getDependJarFiles(String gitUrl) throws GitAPIException, MavenInvocationException, IOException {
        //clone repository do lokalniho temp adresare
        Path projectPath = Files.createTempDirectory("");
        Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(projectPath.toFile())
                .call();

        //download maven zavislosti projektu
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectPath.toString() + File.separator + "pom.xml" ) );
        request.setGoals( Arrays.asList( "dependency:copy-dependencies") );
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File("/usr/share/maven")); //System.getenv("M3_HOME")
        invoker.execute(request);

        //vyfiltrovani a vraceni .jar souboru
        File dependenciesDir = new File(projectPath.toString() + File.separator + "target" + File.separator + "dependency");
        FileFilter fileFilter = file -> file.getName().endsWith(".jar");
        return dependenciesDir.listFiles(fileFilter);
    }

}
