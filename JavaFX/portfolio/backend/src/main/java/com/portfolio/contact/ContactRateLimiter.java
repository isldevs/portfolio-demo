package com.portfolio.contact;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContactRateLimiter {
    private static final long WINDOW_MILLIS = Duration.ofHours(1).toMillis();
    private static final int MAX_REQUESTS_PER_WINDOW = 5;
    private final ConcurrentHashMap<String, Bucket> requests = new ConcurrentHashMap<>();
    private final Clock clock;

    public ContactRateLimiter() {
        this(Clock.systemUTC());
    }

    ContactRateLimiter(Clock clock) {
        this.clock = clock;
    }

    public boolean allow(String remoteIp) {
        long now = clock.millis();
        Bucket updated = requests.compute(remoteIp, (key, bucket) -> {
            if (bucket == null || now - bucket.since() >= WINDOW_MILLIS) {
                return new Bucket(now, 1);
            }
            return new Bucket(bucket.since(), bucket.count() + 1);
        });
        return updated.count() <= MAX_REQUESTS_PER_WINDOW;
    }

    @Scheduled(fixedDelay = 3_600_000)
    public void removeExpired() {
        long now = clock.millis();
        requests.entrySet().removeIf(entry -> now - entry.getValue().since() >= WINDOW_MILLIS);
    }

    private record Bucket(long since, int count) { }
}
