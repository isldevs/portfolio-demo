package com.portfolio.desktop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class PortfolioApiClient {
    private final URI baseUri;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public PortfolioApiClient(String baseUrl) {
        String normalized = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.baseUri = URI.create(normalized);
        if (!"https".equalsIgnoreCase(baseUri.getScheme()) && !"http".equalsIgnoreCase(baseUri.getScheme())) {
            throw new IllegalArgumentException("PORTFOLIO_API_URL must use http or https");
        }
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.mapper = new ObjectMapper();
    }

    public String getBaseUrl() { return baseUri.toString(); }

    public CompletableFuture<JsonNode> getPortfolio() {
        HttpRequest request = HttpRequest.newBuilder(baseUri.resolve("api/v1/portfolio"))
                .GET().timeout(Duration.ofSeconds(15)).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(response -> {
                    if (response.statusCode() != 200) throw new IllegalStateException("API returned HTTP " + response.statusCode());
                    try { return mapper.readTree(response.body()); }
                    catch (IOException e) { throw new IllegalStateException("Invalid portfolio JSON", e); }
                });
    }

    public CompletableFuture<Void> sendContact(String name, String email, String subject, String message) {
        try {
            String json = mapper.writeValueAsString(new ContactPayload(name, email, subject, message, ""));
            HttpRequest request = HttpRequest.newBuilder(baseUri.resolve("api/v1/contact"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8)).build();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding()).thenAccept(response -> {
                if (response.statusCode() == 429) throw new IllegalStateException("Too many messages. Try again later.");
                if (response.statusCode() != 201) throw new IllegalStateException("Server rejected message (HTTP " + response.statusCode() + ")");
            });
        } catch (Exception e) { return CompletableFuture.failedFuture(e); }
    }

    private record ContactPayload(String name, String email, String subject, String message, String website) { }
}
