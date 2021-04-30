package hrubyj;

import freemarker.template.*;

import java.io.*;
import java.util.Locale;
import java.util.Map;


public class ReportPrinter {

    private final Configuration config;

    public ReportPrinter() {
        config = new Configuration(Configuration.VERSION_2_3_31);
        config.setClassForTemplateLoading(ReportPrinter.class, "templates");
        config.setIncompatibleImprovements(new Version(2, 3, 20));
        config.setDefaultEncoding("UTF-8");
        config.setLocale(Locale.US);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void printReport(Map data) throws IOException, TemplateException {
        Template template = config.getTemplate("reportTemplate.ftl");

        File reportFile = new File(  "GlobalReport.html");
        try (PrintStream stream = new PrintStream(reportFile)) {
            Writer out = new OutputStreamWriter(stream);
            template.process(data, out);
        }

    }



}
