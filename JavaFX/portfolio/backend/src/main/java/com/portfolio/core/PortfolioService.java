package com.portfolio.core;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PortfolioService {
    private final String content;

    public PortfolioService() throws IOException {
        try (var stream = new ClassPathResource("portfolio.json").getInputStream()) {
            content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public String getPortfolioJson() {
        return content;
    }
}
