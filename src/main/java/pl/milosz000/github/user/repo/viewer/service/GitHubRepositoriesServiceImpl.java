package pl.milosz000.github.user.repo.viewer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.milosz000.github.user.repo.viewer.dto.GitHubBranchResponseDTO;
import pl.milosz000.github.user.repo.viewer.dto.GitHubRepoResponseDTO;
import pl.milosz000.github.user.repo.viewer.dto.GitHubUserRepositoriesInfoResponseDTO;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubNotFoundException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubUserNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubRepositoriesServiceImpl implements GitHubRepositoriesService {

    private final GitHubApiService githubApiService;

    @Override
    public Flux<GitHubUserRepositoriesInfoResponseDTO> getDetailsForUser(String username) throws IOException {
        List<GitHubRepoResponseDTO> userNonForkedRepositories = getUserRepositories(username);
        return getRepositoryBranchesInfo(userNonForkedRepositories);
    }

    private List<GitHubRepoResponseDTO> getUserRepositories(String username) throws IOException {
        String apiUrl = String.format("https://api.github.com/users/%s/repos", username);

        try {
            String jsonResponse = githubApiService.makeApiCall(apiUrl);

            TypeReference<List<GitHubRepoResponseDTO>> typeRef = new TypeReference<>() {};
            List<GitHubRepoResponseDTO> repositories = parseGitHubResponse(jsonResponse, typeRef);

            return repositories.stream()
                    .filter(repo -> !repo.isFork())
                    .toList();

        } catch (GitHubNotFoundException e) {
            throw new GitHubUserNotFoundException(String.format("Cannot found user with username: %s", username));
        }
    }

    private Flux<GitHubUserRepositoriesInfoResponseDTO> getRepositoryBranchesInfo(List<GitHubRepoResponseDTO> userRepositories) {
        return Flux.fromIterable(userRepositories) // Create a Flux from the userRepositories list
                .flatMap(repo -> {
                    String owner = repo.getOwner().getLogin();
                    String repoName = repo.getName();

                    // Call the getBranches method to get branches reactively
                    return githubApiService.getBranches(owner, repoName)
                            .flatMap(response -> {
                                // Deserialize the response into a list of GitHubBranchResponseDTO
                                TypeReference<List<GitHubBranchResponseDTO>> typeRef = new TypeReference<>() {};
                                List<GitHubBranchResponseDTO> branches;
                                try {
                                    branches = parseGitHubResponse(response, typeRef);
                                } catch (JsonProcessingException e) {
                                    return Mono.error(new RuntimeException("Error parsing branch response", e)); // Handle parsing error
                                }

                                // Return the info for this repository wrapped in a Mono
                                return Mono.just(setUserRepositoriesInfo(repo, branches));
                            });
                });
    }


    private <T> List<T> parseGitHubResponse(String jsonResponse, TypeReference<List<T>> typeReference) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, typeReference);
    }

    private GitHubUserRepositoriesInfoResponseDTO setUserRepositoriesInfo(GitHubRepoResponseDTO repository, List<GitHubBranchResponseDTO> branches) {
        GitHubUserRepositoriesInfoResponseDTO response = new GitHubUserRepositoriesInfoResponseDTO();
        List<GitHubUserRepositoriesInfoResponseDTO.GitHubBranchDetailsDTO> branchesDetails = new LinkedList<>();

        response.setRepositoryName(repository.getName());
        response.setOwnerLogin(repository.getOwner().getLogin());

        branches.forEach(branch -> {
            GitHubUserRepositoriesInfoResponseDTO.GitHubBranchDetailsDTO branchDetails = new GitHubUserRepositoriesInfoResponseDTO.GitHubBranchDetailsDTO();
            branchDetails.setBranchName(branch.getName());
            branchDetails.setLastCommitSHA(branch.getCommit().getSha());

            branchesDetails.add(branchDetails);
        });

        response.setBranches(branchesDetails);

        response.setBranches(branchesDetails);

        return response;
    }
}
