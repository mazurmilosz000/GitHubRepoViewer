package pl.milosz000.github.user.repo.viewer.exceptions;

public class GitHubApiException extends RuntimeException {

    public GitHubApiException(String message) {
        super(message);
    }
}
