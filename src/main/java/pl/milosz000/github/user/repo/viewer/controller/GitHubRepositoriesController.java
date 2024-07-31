package pl.milosz000.github.user.repo.viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.milosz000.github.user.repo.viewer.dto.GitHubUserRepositoriesInfoResponseDTO;
import pl.milosz000.github.user.repo.viewer.service.GitHubRepositoriesService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
public class GitHubRepositoriesController {

    private final GitHubRepositoriesService gitHubRepositoriesService;

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<List<GitHubUserRepositoriesInfoResponseDTO>> getRepositories(@PathVariable("username") String username) throws IOException {
        return new ResponseEntity<>(gitHubRepositoriesService.getDetailsForUser(username), HttpStatusCode.valueOf(200));
    }
}
