package cz.zcu.kiv.service;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;

/**
 * Invokes Maven commands.
 */
public class MavenService {

    /** path to project */
    private String projectPath;

    /**
     * Constructor
     *
     * @param projectPath path to project
     */
    public MavenService(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * Invokes command "mvn package -DskipTests".
     *
     * @throws MavenInvocationException
     */
    public void packageApp() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectPath + File.separator + "pom.xml"));
        request.setGoals( Arrays.asList("package -DskipTests"));
        request.setTimeoutInSeconds(3600);
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("M2_HOME")));
        invoker.execute(request);
    }

    /**
     * Invokes command "mvn dependency:copy-dependencies".
     *
     * @throws MavenInvocationException
     */
    public void copyDependencies() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( projectPath + File.separator + "pom.xml"));
        request.setGoals( Arrays.asList( "dependency:copy-dependencies") );
        request.setTimeoutInSeconds(3600);
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("M2_HOME")));
        invoker.execute(request);
    }


}
