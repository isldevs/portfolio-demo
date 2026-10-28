package dev.portfolio.ui.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.button.Button;
import dev.portfolio.ui.Ui;

final class AdminUi {
    private AdminUi() {}
    static Div page(String title, String intro, Component... content) {
        Div inner = Ui.div("admin-content", content);
        return Ui.div("admin-page page-container", Ui.pageTitle("PORTFOLIO / ADMIN", title, intro), inner);
    }
    static Button primary(String label, Runnable action) {
        Button b = new Button(label, e -> action.run()); b.addClassName("admin-primary"); return b;
    }
    static Button danger(String label, Runnable action) {
        Button b = new Button(label, e -> action.run()); b.addClassName("admin-danger"); return b;
    }
    static void fail(Exception ex) { Notification.show(ex.getMessage(), 6000, Notification.Position.TOP_CENTER); }
    static void success(String text) { Notification.show(text, 3500, Notification.Position.TOP_CENTER); }
    static Dialog dialog(String title, Component... children) {
        Dialog d = new Dialog();
        d.setHeaderTitle(title);
        d.setWidth("min(92vw, 670px)");
        Div form = Ui.div("admin-dialog-form", children);
        d.add(form);
        return d;
    }
    static void confirm(String message, Runnable onConfirm) {
        Dialog dialog = dialog("Confirm deletion", Ui.paragraph(message));
        Button cancel = new Button("Cancel", e -> dialog.close());
        Button remove = danger("Delete permanently", () -> { onConfirm.run(); dialog.close(); });
        dialog.getFooter().add(cancel, remove); dialog.open();
    }
}
