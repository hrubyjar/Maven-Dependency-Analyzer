package hrubyj;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.nio.file.Path;

public class GitRepositoryDownloader {


    public Path cloneGitRepository(String gitUrl) throws GitAPIException {
        Path projectPath = new File("/home/hrubyj/Plocha/testrepo").toPath(); // Files.createTempDirectory("");
        Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(projectPath.toFile())
                .call();

        return projectPath;
    }


}
