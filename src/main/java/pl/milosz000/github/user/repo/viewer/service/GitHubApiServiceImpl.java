package pl.milosz000.github.user.repo.viewer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubApiException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubNotFoundException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubUnauthorizeException;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Service
public class GitHubApiServiceImpl implements GitHubApiService {

    @Value("${app.token}")
    private String jwtToken;

    private final WebClient webClient;

    public GitHubApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
    }

    @Override
    public Mono<String> getRepositories(String username) {
        return configureRequest((WebClient.RequestHeadersUriSpec<?>) webClient.get().uri("/users/{username}/repos", username))
                .retrieve()
                .onStatus(status -> status.value() == 404, clientResponse -> Mono.error(new GitHubNotFoundException()))
                .onStatus(status -> status.value() == 401, clientResponse -> Mono.error(new GitHubUnauthorizeException("An error occurred during API authorization. Verify your API token and try again.")))
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new GitHubApiException("An unknown error occurred while making an API call!")))
                .bodyToMono(String.class);
    }

    public Mono<String> getBranches(String owner, String repo) {
        log.info("Get branches for repo: {}", repo);
        return configureRequest((WebClient.RequestHeadersUriSpec<?>) webClient.get().uri("/repos/{owner}/{repo}/branches", owner, repo))
                .retrieve()
                .bodyToMono(String.class);
    }

    private WebClient.RequestHeadersSpec<?> configureRequest(WebClient.RequestHeadersUriSpec<?> requestSpec) {
        return requestSpec
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "Bearer " + jwtToken)
                .header("X-GitHub-Api-Version", "2022-11-28");
    }
}
