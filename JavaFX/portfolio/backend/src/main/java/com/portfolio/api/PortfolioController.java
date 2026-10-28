package com.portfolio.api;

import com.portfolio.core.PortfolioService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PortfolioController {
    private final PortfolioService service;

    public PortfolioController(PortfolioService service) {
        this.service = service;
    }

    @GetMapping(value = "/portfolio", produces = MediaType.APPLICATION_JSON_VALUE)
    public String portfolio() {
        return service.getPortfolioJson();
    }
}
