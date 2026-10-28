package dev.portfolio.ui;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.portfolio.model.Project;
import dev.portfolio.service.PortfolioService;

@Route(value = "projects/:slug", layout = SiteLayout.class)
@PageTitle("Project Details | Portfolio")
@AnonymousAllowed
public class ProjectDetailView extends Div implements BeforeEnterObserver {
    private final PortfolioService service;
    public ProjectDetailView(PortfolioService service) { this.service=service; addClassName("public-page"); }

    @Override public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        String slug = event.getRouteParameters().get("slug").orElse("");
        Project p = service.project(slug).orElse(null);
        if (p == null) {
            add(Ui.container(Ui.pageTitle("NOT FOUND", "Project not found", "This project may have been removed."),
                    Ui.link("/projects", "← Back to projects", "btn btn-outline"), Ui.footer()));
            return;
        }
        Div tech = Ui.div("tech-tags");
        for (String tag : p.getTechnology().split(",")) if (!tag.isBlank()) tech.add(Ui.span("tech-tag", tag.strip()));
        Div links = Ui.div("button-row");
        if (p.getSourceUrl().startsWith("https://") || p.getSourceUrl().startsWith("http://"))
            links.add(Ui.external(p.getSourceUrl(), "Source code ↗", "btn btn-outline"));
        if (p.getDemoUrl().startsWith("https://") || p.getDemoUrl().startsWith("http://"))
            links.add(Ui.external(p.getDemoUrl(), "Live demo ↗", "btn btn-primary"));
        add(Ui.container(Ui.link("/projects", "← All projects", "text-link"),
                Ui.pageTitle("PROJECT / CASE STUDY", p.getTitle(), p.getSummary()),
                Ui.div("detail-content", Ui.span("eyebrow", "TECHNOLOGY"), tech,
                    Ui.heading("About this project"), Ui.paragraph(p.getDetails()), links), Ui.footer()));
    }
}
