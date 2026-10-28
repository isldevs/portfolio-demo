package dev.portfolio.ui.admin;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.portfolio.service.*;
import dev.portfolio.ui.Ui;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = AdminLayout.class)
@PageTitle("Dashboard | Portfolio CMS")
@RolesAllowed("ADMIN")
public class AdminDashboardView extends Div {
    public AdminDashboardView(PortfolioService portfolio, ContactService contact) {
        Div stats = Ui.div("admin-stats",
                stat("Projects", String.valueOf(portfolio.projects().size())),
                stat("Skills", String.valueOf(portfolio.skills().size())),
                stat("Experience entries", String.valueOf(portfolio.experiences().size())),
                stat("Unread messages", String.valueOf(contact.unread())));
        add(AdminUi.page("Overview", "Everything you need to maintain your public portfolio.",
                stats, Ui.div("admin-info", new H3("Welcome to your content manager"),
                        Ui.paragraph("Use the navigation to change your portfolio profile, publish projects, edit skills and experience, or read messages."),
                        Ui.link("/", "View live website  ↗", "btn btn-primary"))));
    }
    private static Div stat(String label, String count) {
        return Ui.div("admin-stat", Ui.span("stat-number", count), Ui.span("stat-label", label));
    }
}
