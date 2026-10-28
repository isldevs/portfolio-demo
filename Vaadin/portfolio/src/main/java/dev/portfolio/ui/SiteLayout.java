package dev.portfolio.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.portfolio.service.PortfolioService;

@AnonymousAllowed
public class SiteLayout extends AppLayout {
    public SiteLayout(PortfolioService service) {
        setPrimarySection(Section.NAVBAR);
        String title = service.profile().getFullName();
        Anchor brand = Ui.link("/", title, "brand");
        Div dot = Ui.div("brand-dot");
        Div brandGroup = Ui.div("brand-group", dot, brand);
        Div nav = Ui.div("nav-links",
                nav("Home", HomeView.class),
                nav("Projects", ProjectsView.class),
                nav("About", AboutView.class),
                nav("Contact", ContactView.class));
        Anchor admin = Ui.link("/admin", "Admin ↗", "nav-admin");
        Div bar = Ui.div("topbar page-container", brandGroup, nav, admin);
        addToNavbar(bar);
    }
    private static RouterLink nav(String title, Class<? extends com.vaadin.flow.component.Component> view) {
        RouterLink link = new RouterLink(title, view); link.addClassName("nav-link"); return link;
    }
}
