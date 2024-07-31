package pl.milosz000.github.user.repo.viewer.service;

import java.io.IOException;

public interface GitHubApiService {

    String makeApiCall(String apiUrl) throws IOException;
}
