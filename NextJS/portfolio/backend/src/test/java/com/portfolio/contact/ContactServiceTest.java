package com.portfolio.contact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ContactServiceTest {
    private ContactRepository repository;
    private ContactService service;

    @BeforeEach
    void setup() {
        repository = mock(ContactRepository.class);
        service = new ContactService(repository, "an-example-test-salt-longer-than-thirty-two-characters");
    }

    @Test
    void savesValidMessage() {
        when(repository.countByClientHashAndCreatedAtAfter(anyString(), any())).thenReturn(0L);
        service.submit(new ContactRequest("Ada Lovelace", "Ada@Example.com", "Hello", "I'd like to work together.", ""), "127.0.0.1");
        verify(repository).save(any(ContactMessage.class));
    }

    @Test
    void rejectsAfterFiveMessagesPerHour() {
        when(repository.countByClientHashAndCreatedAtAfter(anyString(), any())).thenReturn(5L);
        assertThrows(ResponseStatusException.class, () -> service.submit(
                new ContactRequest("Ada", "ada@example.com", "Hello", "A valid message body.", ""), "127.0.0.1"));
        verify(repository, never()).save(any());
    }

    @Test
    void honeypotSkipsSaving() {
        service.submit(new ContactRequest("Bot", "bot@example.com", "Hello", "This is a spam message.", "fake-website"), "127.0.0.1");
        verifyNoInteractions(repository);
    }
}
