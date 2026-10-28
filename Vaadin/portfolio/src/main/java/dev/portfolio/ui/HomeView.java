package dev.portfolio.ui;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.portfolio.model.*;
import dev.portfolio.service.PortfolioService;

import java.util.List;

@PageTitle("Home | Portfolio")
@Route(value = "", layout = SiteLayout.class)
@AnonymousAllowed
public class HomeView extends Div {
    public HomeView(PortfolioService service) {
        Profile profile = service.profile();
        addClassName("public-page");
        add(Ui.container(
            Ui.div("hero",
                Ui.div("hero-copy",
                    Ui.span("eyebrow", "●  " + profile.getAvailability()),
                    Ui.div("hero-headline", heroHeading(profile.getFullName())),
                    Ui.div("hero-subtitle", new H2(profile.getHeadline())),
                    Ui.paragraph(profile.getIntroduction()),
                    Ui.div("button-row", Ui.link("/projects", "Explore my work  →", "btn btn-primary"),
                        Ui.link("/contact", "Get in touch", "btn btn-outline")),
                    Ui.div("hero-social", Ui.external(profile.getGithubUrl(), "GitHub ↗", "text-link"),
                        Ui.external(profile.getLinkedinUrl(), "LinkedIn ↗", "text-link"))
                ),
                Ui.div("hero-visual",
                    Ui.div("orb orb-one"), Ui.div("orb orb-two"),
                    Ui.div("visual-panel",
                        Ui.span("visual-label", "PORTFOLIO / 2026"),
                        Ui.div("visual-monogram", new Span(initials(profile.getFullName()))),
                        Ui.span("visual-caption", "Engineering digital experiences"),
                        Ui.span("visual-bottom", "JAVA  ·  VAADIN  ·  SPRING BOOT")
                    )
                )
            ),
            Ui.div("stats-strip",
                stat("01", "One Java stack"), stat("100%", "Server-side UI"),
                stat("∞", "Room to grow")
            ),
            projectsSection(service.featuredProjects()),
            skillsSection(service.skills()),
            Ui.section(Ui.div("cta-banner",
                Ui.span("eyebrow", "LET'S COLLABORATE"),
                Ui.heading("Have something interesting in mind?"),
                Ui.paragraph("Let's turn ideas into stable, useful software."),
                Ui.link("/contact", "Start a conversation  →", "btn btn-light")
            )), Ui.footer()
        ));
    }
    private static H1 heroHeading(String name) {
        H1 h = new H1(); h.add("Hi, I'm "); Span accent = Ui.span("gradient-text", name); h.add(accent);
        return h;
    }
    private static String initials(String name) {
        StringBuilder sb = new StringBuilder();
        for (String part : name.strip().split("\\s+")) if (!part.isBlank() && sb.length()<2) sb.append(Character.toUpperCase(part.charAt(0)));
        return sb.toString();
    }
    private static Div stat(String number, String caption) {
        return Ui.div("stat-item", Ui.span("stat-number", number), Ui.span("stat-label", caption));
    }
    private static Div projectsSection(List<Project> projects) {
        Div grid = Ui.div("project-grid");
        for (Project p : projects) grid.add(projectCard(p));
        if (projects.isEmpty()) grid.add(Ui.paragraph("Projects coming soon."));
        return Ui.section(
                Ui.div("section-header", Ui.div("section-heading", Ui.span("eyebrow", "SELECTED WORK"),
                        Ui.heading("Projects I'm proud of")),
                        Ui.link("/projects", "View all projects  ↗", "text-link")), grid);
    }
    public static Div projectCard(Project p) {
        Div labels = Ui.div("tech-tags");
        for (String tag : p.getTechnology().split(",")) if (!tag.isBlank()) labels.add(Ui.span("tech-tag", tag.strip()));
        return Ui.div("project-card",
                Ui.div("project-card-top", Ui.span("project-id", String.format("%02d", p.getSortOrder())),
                        Ui.span("project-arrow", "↗")),
                Ui.div("project-card-content", new H3(p.getTitle()), Ui.paragraph(p.getSummary()), labels,
                        Ui.link("/projects/" + p.getSlug(), "View case study →", "card-link"))
        );
    }
    private static Div skillsSection(List<Skill> skills) {
        Div chips = Ui.div("skill-cloud");
        for (Skill skill : skills) chips.add(Ui.span("skill-pill", skill.getName()));
        return Ui.section(Ui.span("eyebrow", "TOOLS & TECHNOLOGIES"),
                Ui.heading("The stack behind the work"),
                Ui.paragraph("A practical toolkit for building, shipping, and maintaining software."), chips);
    }
}
