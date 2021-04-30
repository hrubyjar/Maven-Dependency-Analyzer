package hrubyj;

import com.verifa.checker.VerifaChecker;
import com.verifa.checker.VerifaConformancePrinter;
import com.verifa.checker.VerifaReports;
import com.verifa.commons.utils.JavaFileUtils;
import com.verifa.flow.VerifaFlow;
import com.verifa.jacc.ccu.ApiInterCompatibilityResult;
import com.verifa.jacc.ccu.ClassFilter;

import java.io.*;
import java.nio.file.Path;


public class Analyzer {

    private final Path projectPath;

    public Analyzer(Path projectPath) {
        this.projectPath = projectPath;
    }

    public ApiInterCompatibilityResult doAnalysis(String projectName, String appFilesPath, String libFilesPath) throws InterruptedException, IOException {
        System.out.println("Executing analyzer!");
        File[] appFiles = JavaFileUtils.listBytecodeFiles(new File(appFilesPath));
        File[] libFiles = JavaFileUtils.listBytecodeFiles(new File(libFilesPath));
        File destFolderFile = new File("reports/");
        File[] extraFiles = new File[0];
        String reportName = projectName;
        ClassFilter classFilter = ClassFilter.TRUE_FILTER;

        VerifaChecker.Builder builder = new VerifaChecker.Builder(appFiles, libFiles, reportName, destFolderFile)
                .extraFiles(extraFiles)
                .filter(classFilter);
        // --skip-flow
        VerifaFlow flow = new VerifaFlow.Builder(appFiles, libFiles).extraFiles(extraFiles).build();
        builder.flow(flow.compute());

        VerifaChecker checker = builder.build();
        VerifaReports verifaReports = new VerifaReports(checker);
        VerifaConformancePrinter printer = verifaReports.apiConformancePrinter();
        printer.printHtml(false, true);

        return checker.checkApiConformance().getInterCompatibilityAPI();
    }



}
