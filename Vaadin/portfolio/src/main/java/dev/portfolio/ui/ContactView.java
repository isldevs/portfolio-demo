package dev.portfolio.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.portfolio.service.ContactService;
import dev.portfolio.service.PortfolioService;

import java.time.Duration;
import java.time.Instant;

@Route(value = "contact", layout = SiteLayout.class)
@PageTitle("Contact | Portfolio")
@AnonymousAllowed
public class ContactView extends Div {
    private static final String SESSION_KEY = "last-contact-message-at";
    public ContactView(ContactService contacts, PortfolioService portfolio) {
        addClassName("public-page");
        TextField name = new TextField("Your name"); name.setRequired(true); name.setMaxLength(120);
        EmailField email = new EmailField("Email address"); email.setRequired(true); email.setMaxLength(254);
        TextField subject = new TextField("Subject"); subject.setRequired(true); subject.setMaxLength(160);
        TextArea message = new TextArea("Tell me about your project"); message.setRequired(true);
        message.setMaxLength(5000); message.setMinHeight("170px");
        name.setPlaceholder("Jane Doe"); email.setPlaceholder("jane@example.com");
        subject.setPlaceholder("I'd like to work together");
        message.setPlaceholder("Tell me what you're building...");
        Button submit = new Button("Send message  →"); submit.addClassName("btn-submit");
        submit.addClickListener(event -> {
            Instant previous = (Instant) VaadinSession.getCurrent().getAttribute(SESSION_KEY);
            if (previous != null && Duration.between(previous, Instant.now()).toSeconds() < 60) {
                Notification.show("Please wait one minute before sending another message.", 5000, Notification.Position.TOP_CENTER);
                return;
            }
            try {
                if (email.isInvalid()) throw new IllegalArgumentException("Please enter a valid email address");
                contacts.submit(name.getValue(), email.getValue(), subject.getValue(), message.getValue());
                VaadinSession.getCurrent().setAttribute(SESSION_KEY, Instant.now());
                name.clear(); email.clear(); subject.clear(); message.clear();
                Notification.show("Thanks! Your message has been received.", 6000, Notification.Position.TOP_CENTER);
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
            } catch (Exception ex) {
                Notification.show("Could not send your message. Please try again later.", 5000, Notification.Position.TOP_CENTER);
            }
        });
        Div form = Ui.div("contact-form", name, email, subject, message, submit,
                Ui.span("form-hint", "Messages are stored privately and can be read from the admin dashboard."));
        Div info = Ui.div("contact-aside", Ui.span("eyebrow", "START A CONVERSATION"),
                Ui.heading("Let's build something useful."),
                Ui.paragraph("Have a project, question, or collaboration in mind? Send a message below."),
                Ui.div("contact-direct", Ui.span("eyebrow", "OR EMAIL ME DIRECTLY"),
                        Ui.link("mailto:" + portfolio.profile().getEmail(), portfolio.profile().getEmail(), "text-link")));
        add(Ui.container(Ui.pageTitle("CONTACT / SAY HELLO", "Get in touch", "Good projects start with a conversation."),
                Ui.div("contact-layout", info, form), Ui.footer()));
    }
}
