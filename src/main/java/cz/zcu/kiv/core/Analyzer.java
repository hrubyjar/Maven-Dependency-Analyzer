package cz.zcu.kiv.core;

import com.verifa.checker.VerifaChecker;
import com.verifa.checker.VerifaConformancePrinter;
import com.verifa.checker.VerifaReports;
import com.verifa.commons.utils.JavaFileUtils;
import com.verifa.flow.VerifaFlow;
import com.verifa.jacc.ccu.ApiInterCompatibilityResult;
import com.verifa.jacc.ccu.ClassFilter;
import cz.zcu.kiv.service.GitService;
import cz.zcu.kiv.service.MavenService;
import cz.zcu.kiv.service.ResultService;
import cz.zcu.kiv.service.vo.ResultVO;
import cz.zcu.kiv.storage.DBConnector;
import cz.zcu.kiv.utils.FileSearcher;
import cz.zcu.kiv.utils.JSONGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHException;
import org.kohsuke.github.GHRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Performs the analysis.
 */
public class Analyzer {

    /**
     * Analyses project.
     *
     * @param projectName project name
     * @param appFilesDirectory path to app files directory
     * @param libFilesDirectory path to lib files directory
     * @throws IOException
     */
    public void analyseProject(String projectName, File appFilesDirectory, File libFilesDirectory) throws IOException {
        ResultService resultService = new ResultService();
        File reportsFolder = Files.createTempDirectory("reports").toFile();
        JSONGenerator jsonGenerator = new JSONGenerator();
        DBConnector dbConnector = new DBConnector();

        try {
            ApiInterCompatibilityResult compatibilityResult = executeAnalysis(projectName, appFilesDirectory.getAbsolutePath(),
                    libFilesDirectory.getAbsolutePath(), reportsFolder);
            if (compatibilityResult != null) {
                File result = new File(projectName + ".txt");
                ResultVO resultVO = resultService.loadResult(result.getAbsolutePath());
                resultVO.setRepositoryName(projectName);
                result.delete();

                File json = jsonGenerator.generateJSON(compatibilityResult, appFilesDirectory.toString(), libFilesDirectory.toString(), projectName);
                dbConnector.insertNewResult(resultVO, json.getAbsolutePath(), reportsFolder.getAbsolutePath() + File.separator + projectName + ".html");
            }
        } catch (IOException | IllegalStateException e) {
            System.out.println("Error analysing project " + projectName + "!");
        }
    }

    /**
     * Analyse public repositories on GitHub.
     *
     * @param since starting id
     * @param githubToken personal access token
     * @throws IOException
     */
    public void analyseGitHub(Integer since, String githubToken) throws IOException {
        GitService gitService = new GitService(githubToken);
        GHRepository repository = null;
        try {
           repository = gitService.getMavenRepository(since.toString());
        } catch (GHException e) {
            System.out.println("Something went wrong! Please check your personal access token.");
        }

        ResultService resultService = new ResultService();
        File reportsFolder = Files.createTempDirectory("reports").toFile();
        File appFilesDirectory = Files.createTempDirectory("appfiles").toFile();
        File libFilesDirectory = Files.createTempDirectory("libfiles").toFile();
        JSONGenerator jsonGenerator = new JSONGenerator();
        DBConnector dbConnector = new DBConnector();
        FileSearcher fileSearcher;
        MavenService mavenService;
        Path projectPath = null;

        while (repository != null) {
            try {
                projectPath = Files.createTempDirectory(repository.getName());
                gitService.cloneGitRepository(repository.getHtmlUrl() + ".git", projectPath);
                fileSearcher = new FileSearcher(projectPath.toString());
                mavenService = new MavenService(projectPath.toString());

                mavenService.packageApp();
                List<File> appFiles = fileSearcher.findAppFiles();
                mavenService.copyDependencies();
                List<File> libFiles = fileSearcher.findLibFiles();
                copyFilesIntoDirectory(appFiles, appFilesDirectory);
                copyFilesIntoDirectory(libFiles, libFilesDirectory);
                if (appFiles.size() > 0  && libFiles.size() > 0) {
                    String projectName = repository.getFullName().replace("/", "-");
                    ApiInterCompatibilityResult compatibilityResult = executeAnalysis(projectName, appFilesDirectory.getAbsolutePath(),
                            libFilesDirectory.getAbsolutePath(), reportsFolder);
                    if (compatibilityResult != null) {
                        File result = new File(projectName + ".txt");
                        ResultVO resultVO = resultService.loadResult(result.getAbsolutePath());
                        resultVO.setRepositoryName(projectName);
                        result.delete();

                        File json = jsonGenerator.generateJSON(compatibilityResult, appFilesDirectory.toString(), libFilesDirectory.toString(), projectName);
                        dbConnector.insertNewResult(resultVO, json.getAbsolutePath(), reportsFolder.getAbsolutePath() + File.separator + projectName + ".html");
                    }
                }
            } catch (GitAPIException | MavenInvocationException | IOException | IllegalStateException e) {
                System.out.println("Error analysing project " + repository.getFullName() + "!");
                e.printStackTrace();
            }
            if (projectPath != null) {
                try {
                    FileUtils.deleteDirectory(projectPath.toFile());
                    projectPath.toFile().delete();
                } catch (Exception ignored) {
                }
            }

            deleteFilesFromDirectory(appFilesDirectory);
            deleteFilesFromDirectory(libFilesDirectory);

            File file = new File("since.txt");
            file.delete();
            file = new File("since.txt");
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                bufferedWriter.write(String.valueOf(repository.getId()));
                bufferedWriter.flush();
            } catch (IOException e) {
                System.out.println("Error writing \"since.txt\"! " + e);
            }

            repository = gitService.getMavenRepository(String.valueOf(repository.getId()));
        }
    }

    /**
     * Executes the analysis.
     *
     * @param projectName project name
     * @param appFilesPath path to app files directory
     * @param libFilesPath path to lib files directory
     * @param destFolder directory for reports
     * @return analysis result
     * @throws IOException
     */
    public ApiInterCompatibilityResult executeAnalysis(String projectName, String appFilesPath, String libFilesPath, File destFolder) throws IOException {
        System.out.println("Executing analyzer!");
        File[] appFiles = JavaFileUtils.listBytecodeFiles(new File(appFilesPath));
        File[] libFiles = JavaFileUtils.listBytecodeFiles(new File(libFilesPath));
        File[] extraFiles = new File[0];
        ClassFilter classFilter = ClassFilter.TRUE_FILTER;
        VerifaChecker checker;
        try {

            VerifaChecker.Builder builder = new VerifaChecker.Builder(appFiles, libFiles, projectName, destFolder)
                    .extraFiles(extraFiles)
                    .filter(classFilter);
            VerifaFlow flow = new VerifaFlow.Builder(appFiles, libFiles).extraFiles(extraFiles).build();
            builder.flow(flow.compute());

            checker = builder.build();
            VerifaReports verifaReports = new VerifaReports(checker);
            VerifaConformancePrinter printer = verifaReports.apiConformancePrinter();
            printer.printHtml(false, true);
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
        return checker.checkApiConformance().getInterCompatibilityAPI();
    }

    /**
     * Copies files into directory.
     *
     * @param files files
     * @param directory directory
     * @throws IOException
     */
    private void copyFilesIntoDirectory(List<File> files, File directory) throws IOException {
        for (File file : files) {
            FileUtils.copyFile(file, new File(directory.getAbsolutePath() + File.separator + file.getName()));
        }
    }

    /**
     * Deletes files from directory.
     *
     * @param directory directory
     */
    private void deleteFilesFromDirectory(File directory) {
        for (File file : directory.listFiles()) {
            file.delete();
        }
    }


}
