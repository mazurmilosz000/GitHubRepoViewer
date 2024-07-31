package pl.milosz000.github.user.repo.viewer.service;

import reactor.core.publisher.Mono;

import java.io.IOException;

public interface GitHubApiService {

    Mono<String> getRepositories(String username) throws IOException;
    Mono<String> getBranches(String owner, String repo);
}
