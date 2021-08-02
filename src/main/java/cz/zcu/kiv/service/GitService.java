package cz.zcu.kiv.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.*;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Communicates with Git.
 */
public class GitService {

    /** personal access token */
    private String personalAccessToken;

    /**
     * Constructor
     *
     * @param personalAccessToken personal access token
     */
    public GitService(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }

    /**
     * Finds first public repository with pom.xml in root directory.
     *
     * @param since starting index
     * @return repository
     * @throws IOException
     */
    public GHRepository getMavenRepository(String since) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(personalAccessToken).build();
        PagedIterable<GHRepository> repos = github.listAllPublicRepositories(since);
        for (GHRepository repo : repos) {
            try {
                GHContent pom = repo.getFileContent("/pom.xml");
                System.out.println("Maven project found! Repository -> " + repo.getFullName() + ", id: " + repo.getId());
                return repo;
            } catch (Exception e) {
                // pom.xml missing -> not Maven project
            }
        }
        return null;
    }

    /**
     * Clone GitHub repository.
     *
     * @param gitUrl repository url
     * @param projectPath path to project
     * @throws GitAPIException
     * @throws IOException
     */
    public void cloneGitRepository(String gitUrl, Path projectPath) throws GitAPIException, IOException {
        Git.cloneRepository()
                .setURI(gitUrl)
                .setDirectory(projectPath.toFile())
                .call();
    }


}
