package resume.resumegenerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class GitHubService {

    private static final Logger logger = Logger.getLogger(GitHubService.class.getName());

    private final String githubToken;
    private final String githubUsername;
    private final String githubRepo;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GitHubService(
            @Value("${github.token}") String githubToken,
            @Value("${github.username}") String githubUsername,
            @Value("${github.repo}") String githubRepo) {
        this.githubToken = githubToken;
        this.githubUsername = githubUsername;
        this.githubRepo = githubRepo;
    }

    public void uploadFileToGitHub(String fileName, String content) throws IOException, InterruptedException {
        String url = "https://api.github.com/repos/" + githubUsername + "/" + githubRepo + "/contents/" + fileName;

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("message", "Add " + fileName);
        requestBodyMap.put("content", Base64.getEncoder().encodeToString(content.getBytes()));

        String json = objectMapper.writeValueAsString(requestBodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "token " + githubToken)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("GitHub API URL: " + url);
        logger.info("Request Body: " + json);
        logger.info("Response Code: " + response.statusCode());
        logger.info("Response Body: " + response.body());

        if (response.statusCode() != 201) {
            throw new IOException("Failed to upload file: " + response.body());
        }
    }
}
