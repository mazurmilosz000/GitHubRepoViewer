package pl.milosz000.github.user.repo.viewer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubBranchResponseDTO extends GenericGitHubNameResponseDTO {
    private Commit commit;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit extends GenericGitHubNameResponseDTO {
        private String sha;
    }
}
