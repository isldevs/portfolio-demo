package com.portfolio.contact;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactServiceTest {
    private final ContactRepository repository = mock(ContactRepository.class);
    private final ContactRateLimiter limiter = mock(ContactRateLimiter.class);
    private final ContactService service = new ContactService(repository, limiter);

    @Test
    void savesValidHumanMessage() {
        when(limiter.allow("127.0.0.1")).thenReturn(true);
        service.submit(new ContactRequest("Ada", "ADA@Example.com", "Hi", "A message longer than ten chars", ""), "127.0.0.1");
        verify(repository).save(any(ContactMessage.class));
    }

    @Test
    void ignoresHoneypotWithoutSaving() {
        when(limiter.allow("127.0.0.1")).thenReturn(true);
        service.submit(new ContactRequest("Ada", "ada@example.com", "Hi", "A message longer than ten chars", "https://spam.example"), "127.0.0.1");
        verifyNoInteractions(repository);
    }

    @Test
    void blocksRateLimitedMessage() {
        when(limiter.allow("127.0.0.1")).thenReturn(false);
        assertThrows(ResponseStatusException.class,
                () -> service.submit(new ContactRequest("Ada", "ada@example.com", "Hi", "A message longer than ten chars", ""), "127.0.0.1"));
        verifyNoInteractions(repository);
    }
}
