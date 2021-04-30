package hrubyj;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;

public class MavenInvoker {

    private String projectPath;

    public MavenInvoker(String projectPath) {
        this.projectPath = projectPath;
    }

    public void packageApp() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectPath + File.separator + "pom.xml"));
        request.setGoals( Arrays.asList("package -DskipTests"));
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("M3_HOME")));
        invoker.execute(request);
    }

    public void copyDependencies() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectPath + File.separator + "pom.xml"));
        request.setGoals( Arrays.asList( "dependency:copy-dependencies") );
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("M3_HOME")));
        invoker.execute(request);
    }


}
