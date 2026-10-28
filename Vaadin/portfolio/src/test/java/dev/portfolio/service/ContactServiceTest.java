package dev.portfolio.service;

import dev.portfolio.model.ContactMessage;
import dev.portfolio.repo.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactServiceTest {
    private ContactMessageRepository repository;
    private ContactService service;

    @BeforeEach
    void setup() {
        repository = mock(ContactMessageRepository.class);
        service = new ContactService(repository);
    }

    @Test
    void acceptsValidContactAndTrimsInput() {
        service.submit("  Jane Doe  ", " jane@example.com ", " Hello ", " This is a legitimate enquiry about your work. ");
        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
        verify(repository).save(captor.capture());
        ContactMessage saved = captor.getValue();
        assertEquals("Jane Doe", saved.getSenderName());
        assertEquals("jane@example.com", saved.getSenderEmail());
        assertEquals("Hello", saved.getSubject());
    }

    @Test
    void rejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> service.submit("Jane", "invalid-email", "Hello", "This is a reasonable message length."));
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsShortMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> service.submit("Jane", "jane@example.com", "Hello", "short"));
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsTooLongName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.submit("x".repeat(121), "jane@example.com", "Hello", "This is a reasonable message length."));
        verifyNoInteractions(repository);
    }
}
