package pl.milosz000.github.user.repo.viewer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.milosz000.github.user.repo.viewer.dto.GitHubUserRepositoriesInfoResponseDTO;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitHubRepositoriesIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetUserRepositories_Success() {

        webTestClient.get()
                .uri("/repositories/mazurmilosz000")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GitHubUserRepositoriesInfoResponseDTO.class)
                .consumeWith(response -> {
                    assertThat(response.getResponseBody()).isNotNull();
                });
    }

    @Test
    void testGetUserRepositories_NotFound() {
        String incorrectUsername = "kfhsd7s^gfjkd$";

        webTestClient.get()
                .uri("/repositories/{incorrectUsername}", incorrectUsername)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo(String.format("Cannot find user with username: %s", incorrectUsername));

    }
}