package dev.portfolio.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.portfolio.model.Project;
import dev.portfolio.service.PortfolioService;

@Route(value = "projects", layout = SiteLayout.class)
@PageTitle("Projects | Portfolio")
@AnonymousAllowed
public class ProjectsView extends Div {
    public ProjectsView(PortfolioService service) {
        addClassName("public-page");
        Div grid = Ui.div("project-grid");
        for (Project p : service.projects()) grid.add(HomeView.projectCard(p));
        if (service.projects().isEmpty()) grid.add(Ui.paragraph("Projects will appear here soon."));
        add(Ui.container(Ui.pageTitle("WORK / CASE STUDIES", "Selected projects",
                "The work behind the headlines — platforms, products, and problems worth solving."),
                grid, Ui.footer()));
    }
}
