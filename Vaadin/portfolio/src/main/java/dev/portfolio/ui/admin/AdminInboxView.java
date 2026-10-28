package dev.portfolio.ui.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.portfolio.model.ContactMessage;
import dev.portfolio.service.ContactService;
import dev.portfolio.ui.Ui;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;

@Route(value = "admin/inbox", layout = AdminLayout.class)
@PageTitle("Inbox | Portfolio CMS")
@RolesAllowed("ADMIN")
public class AdminInboxView extends Div {
    private final ContactService service;
    private final Grid<ContactMessage> grid = new Grid<>(ContactMessage.class, false);
    public AdminInboxView(ContactService service) {
        this.service=service;
        grid.addColumn(m -> m.isRead() ? "Read" : "New").setHeader("Status");
        grid.addColumn(ContactMessage::getSenderName).setHeader("Sender");
        grid.addColumn(ContactMessage::getSenderEmail).setHeader("Email");
        grid.addColumn(ContactMessage::getSubject).setHeader("Subject");
        grid.addColumn(m -> m.getReceivedAt() == null ? "" :
                m.getReceivedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'"))).setHeader("Received");
        grid.addComponentColumn(m -> AdminUi.primary("Open", () -> show(m))).setHeader("Message");
        grid.setWidthFull(); reload();
        add(AdminUi.page("Inbox", "Contact messages received through the portfolio website.", grid));
    }
    private void reload() { grid.setItems(service.inbox()); }
    private void show(ContactMessage m) {
        Dialog dialog = AdminUi.dialog(m.getSubject(),
                Ui.span("eyebrow", "FROM " + m.getSenderName() + " · " + m.getSenderEmail()),
                Ui.paragraph(m.getMessage()));
        dialog.getFooter().add(new Button("Close", event -> dialog.close()),
                AdminUi.danger("Delete", () -> {
                    AdminUi.confirm("Permanently delete this message?", () -> {
                        try { service.delete(m.getId()); dialog.close(); reload(); AdminUi.success("Message deleted"); }
                        catch (Exception ex) { AdminUi.fail(ex); }
                    });
                }));
        dialog.open();
        if (!m.isRead()) {
            try { service.markRead(m.getId()); reload(); } catch (Exception ex) { AdminUi.fail(ex); }
        }
    }
}
