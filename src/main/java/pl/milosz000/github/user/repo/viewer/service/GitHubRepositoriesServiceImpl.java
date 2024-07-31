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
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubApiException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubNotFoundException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubUnauthorizeException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubUserNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubRepositoriesServiceImpl implements GitHubRepositoriesService {

    private final GitHubApiService githubApiService;

    @Override
    public Flux<GitHubUserRepositoriesInfoResponseDTO> getDetailsForUser(String username) throws IOException {
        return getUserRepositories(username)
                .flatMapMany(this::getRepositoryBranchesInfo);
    }

    private Mono<List<GitHubRepoResponseDTO>> getUserRepositories(String username) throws IOException {

        return githubApiService.getRepositories(username)
                .flatMap(jsonResponse -> {
                    TypeReference<List<GitHubRepoResponseDTO>> typeRef = new TypeReference<>() {};
                    try {
                        List<GitHubRepoResponseDTO> repositories = parseGitHubResponse(jsonResponse, typeRef);
                        // Filter out forked repositories and return an immutable list
                        return Mono.just(repositories.stream()
                                .filter(repo -> !repo.isFork())
                                .toList());
                    } catch (JsonProcessingException e) {
                        return Mono.error(new GitHubApiException("An error occurred while processing json response"));
                    }
                })
                .onErrorMap(e -> switch (e) {
                    case GitHubNotFoundException gitHubNotFoundException ->
                            new GitHubUserNotFoundException(String.format("Cannot find user with username: %s", username));
                    case GitHubUnauthorizeException gitHubUnauthorizeException ->
                            new GitHubUnauthorizeException(e.getMessage());
                    case GitHubApiException gitHubApiException -> new GitHubApiException(e.getMessage());
                    default -> e;
                });

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

        response.setRepositoryName(repository.getName());
        response.setOwnerLogin(repository.getOwner().getLogin());

        // Use streams to create an immutable list of branch details
        List<GitHubUserRepositoriesInfoResponseDTO.GitHubBranchDetailsDTO> branchesDetails = branches.stream()
                .map(branch -> {
                    GitHubUserRepositoriesInfoResponseDTO.GitHubBranchDetailsDTO branchDetails = new GitHubUserRepositoriesInfoResponseDTO.GitHubBranchDetailsDTO();
                    branchDetails.setBranchName(branch.getName());
                    branchDetails.setLastCommitSHA(branch.getCommit().getSha());
                    return branchDetails;
                })
                .toList();

        response.setBranches(branchesDetails);
        return response;
    }

}
