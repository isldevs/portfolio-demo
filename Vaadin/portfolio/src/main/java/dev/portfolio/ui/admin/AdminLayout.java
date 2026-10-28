package dev.portfolio.ui.admin;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import dev.portfolio.ui.Ui;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
public class AdminLayout extends AppLayout {
    private final transient AuthenticationContext auth;
    public AdminLayout(AuthenticationContext auth) {
        this.auth = auth;
        setPrimarySection(Section.NAVBAR);
        Button logout = new Button("Sign out", event -> auth.logout());
        logout.addClassName("admin-logout");
        Div links = Ui.div("admin-nav",
                link("Overview", AdminDashboardView.class),
                link("Profile", AdminProfileView.class),
                link("Projects", AdminProjectsView.class),
                link("Skills", AdminSkillsView.class),
                link("Experience", AdminExperienceView.class),
                link("Inbox", AdminInboxView.class));
        Div navbar = Ui.div("admin-navbar page-container",
                Ui.link("/admin", "◆ PORTFOLIO CMS", "admin-brand"),
                links,
                Ui.div("admin-actions", Ui.link("/", "View website ↗", "text-link"), logout));
        addToNavbar(navbar);
    }
    private static RouterLink link(String label, Class<? extends com.vaadin.flow.component.Component> view) {
        RouterLink l = new RouterLink(label, view); l.addClassName("admin-nav-link"); return l;
    }
}
