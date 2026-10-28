package com.portfolio.contact;

import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.*;

class ContactRateLimiterTest {
    @Test
    void allowsFiveRequestsPerIpThenRejectsTheSixth() {
        var limiter = new ContactRateLimiter(Clock.fixed(Instant.parse("2026-10-28T00:00:00Z"), ZoneOffset.UTC));
        for (int i = 0; i < 5; i++) assertTrue(limiter.allow("127.0.0.1"));
        assertFalse(limiter.allow("127.0.0.1"));
        assertTrue(limiter.allow("127.0.0.2"));
    }
}
