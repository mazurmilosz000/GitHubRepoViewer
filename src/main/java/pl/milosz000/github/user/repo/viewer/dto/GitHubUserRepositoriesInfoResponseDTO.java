package pl.milosz000.github.user.repo.viewer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GitHubUserRepositoriesInfoResponseDTO {
    private String repositoryName;
    private String ownerLogin;
    private List<GitHubBranchDetailsDTO> branches;

    @Getter
    @Setter
    public static class GitHubBranchDetailsDTO {
        private String branchName;
        private String lastCommitSHA;
    }
}


