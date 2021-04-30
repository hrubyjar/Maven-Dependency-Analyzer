package hrubyj;

import org.kohsuke.github.*;
import org.kohsuke.github.authorization.ImmutableAuthorizationProvider;

import java.io.IOException;
import java.util.Map;

public class GitService {


    public static void main(String[] args) throws IOException {
        //GitHub github = GitHub.connect();
        GitHub github = new GitHubBuilder().withOAuthToken("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").build();
        //GitHub github = new GitHubBuilder().withAuthorizationProvider(new ImmutableAuthorizationProvider("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")).build();
        int i = 0;
        PagedIterable<GHRepository> repos = github  .listAllPublicRepositories();
        for (GHRepository repo : repos) {
            try {
                System.out.println("Repository " + i + " -> " + repo.getFullName());
                GHContent pom = repo.getFileContent("/pom.xml");
            } catch (Exception e) {
                System.out.println("Nepustí mi tam, ZMRD!");
            }
                i++;
            if (i == 300) {
                break;
            }
        }
    }


}
