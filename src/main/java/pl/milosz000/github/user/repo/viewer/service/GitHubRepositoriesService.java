package pl.milosz000.github.user.repo.viewer.service;

import pl.milosz000.github.user.repo.viewer.dto.GitHubUserRepositoriesInfoResponseDTO;

import java.io.IOException;
import java.util.List;

public interface GitHubRepositoriesService {
    List<GitHubUserRepositoriesInfoResponseDTO> getDetailsForUser(String username) throws IOException;
}
