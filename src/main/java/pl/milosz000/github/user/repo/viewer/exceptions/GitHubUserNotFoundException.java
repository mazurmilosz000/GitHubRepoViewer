package pl.milosz000.github.user.repo.viewer.exceptions;

public class GitHubUserNotFoundException extends RuntimeException {
    public GitHubUserNotFoundException(String message) {
        super(message);
    }
}
