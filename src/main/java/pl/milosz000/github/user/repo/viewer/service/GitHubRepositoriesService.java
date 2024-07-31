package pl.milosz000.github.user.repo.viewer.service;

import pl.milosz000.github.user.repo.viewer.dto.GitHubUserRepositoriesInfoResponseDTO;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface GitHubRepositoriesService {
    Flux<GitHubUserRepositoriesInfoResponseDTO> getDetailsForUser(String username) throws IOException;
}
