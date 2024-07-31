package pl.milosz000.github.user.repo.viewer.service;

import reactor.core.publisher.Mono;

import java.io.IOException;

public interface GitHubApiService {

    String makeApiCall(String apiUrl) throws IOException;
    Mono<String> getBranches(String owner, String repo);
}
