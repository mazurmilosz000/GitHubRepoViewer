package pl.milosz000.github.user.repo.viewer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepoResponseDTO extends GenericGitHubNameResponseDTO {

    @JsonProperty("full_name")
    private String fullName;

    private Owner owner;

    @JsonProperty("private")
    private boolean privateRepository;
    private boolean fork;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {
        private String login;
    }
}
