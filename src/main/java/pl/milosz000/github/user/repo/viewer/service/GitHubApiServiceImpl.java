package pl.milosz000.github.user.repo.viewer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubApiException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubNotFoundException;
import pl.milosz000.github.user.repo.viewer.exceptions.GitHubUnauthorizeException;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Slf4j
@Service
public class GitHubApiServiceImpl implements GitHubApiService {

    @Value("${app.token}")
    private String jwtToken;

    WebClient webClient = WebClient.create("https://api.github.com");

    @Override
    public String makeApiCall(String apiUrl) throws IOException {
        log.info("Trying to make api call to {}", apiUrl);
        URI uri = URI.create(apiUrl);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new GitHubNotFoundException();

        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new GitHubUnauthorizeException("An error occurred during API authorization. Verify your API token and try again.");

        } else {
            throw new GitHubApiException("An unknown error occurred while making an API call!");
        }
    }

    public Mono<String> getBranches(String owner, String repo) {
        log.info("Get branches for repo: {}", repo);

        return webClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "Bearer " + jwtToken)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .bodyToMono(String.class);

    }
}
