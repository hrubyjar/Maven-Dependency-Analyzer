package hrubyj;

import com.verifa.jacc.ccu.ApiInterCompatibilityResult;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    // test url:
    // https://github.com/apache/maven-antrun-plugin.git
    // https://github.com/hrubyjar/MavenRepo.git
    // https://github.com/dzikoysk/reposilite.git

    static Path projectPath = Paths.get("/home/hrubyj/Plocha/testrepo");

    public static void main(String[] args) throws Exception {
//        String url = "https://github.com/dzikoysk/vlastnirepo.git";
////        GitRepositoryDownloader gitRepositoryDownloader = new GitRepositoryDownloader();
////        projectPath = gitRepositoryDownloader.cloneGitRepository(url);
//        MavenInvoker mavenInvoker = new MavenInvoker(projectPath.toString());
//        Searcher searcher = new Searcher(projectPath.toString());
//        Analyzer analyzer = new Analyzer(projectPath);
        StatsHandler statsHandler = new StatsHandler();
//        JSONGenerator jsonGenerator = new JSONGenerator();
//
////        mavenInvoker.packageApp();
////        List<File> appFiles = searcher.findAppFiles();
////        mavenInvoker.copyDependencies();
////        List<File> libFiles = searcher.findLibFiles();
//
//        File appFilesDirectory = new File("/home/hrubyj/Plocha/copy/appFiles");
//        File libFilesDirectory = new File("/home/hrubyj/Plocha/copy/libFiles");
////        File appFilesDirectory = Files.createTempDirectory("").toFile();
////        File libFilesDirectory = Files.createTempDirectory("").toFile();
//
////        for (File appFile : appFiles) {
////            FileUtils.copyFile(appFile, new File(appFilesDirectory + File.separator + appFile.getName()));
////        }
////
////        for (File libFile : libFiles) {
////            FileUtils.copyFile(libFile, new File(libFilesDirectory + File.separator + libFile.getName()));
////        }
//
//        String[] splitUrl = url.split("/");
//        String projectName = splitUrl[splitUrl.length-1];
//        projectName = projectName.substring(0, projectName.length()-4);
//        ApiInterCompatibilityResult compatibilityResult = analyzer.doAnalysis(projectName, appFilesDirectory.getAbsolutePath(), libFilesDirectory.getAbsolutePath());
        statsHandler.saveToGlobalStats( "vlastnirepo.txt");

//        jsonGenerator.generateJSON(compatibilityResult, appFilesDirectory.toString(), libFilesDirectory.toString(), projectName);
        ReportPrinter reportPrinter = new ReportPrinter();
        Map data = new HashMap<>();
        data.put("statsHandler", statsHandler);
        data.put("C1", new Integer(1));
        reportPrinter.printReport(data);

    }
}
