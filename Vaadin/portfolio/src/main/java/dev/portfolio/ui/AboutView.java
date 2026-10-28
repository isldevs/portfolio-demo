package dev.portfolio.ui;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.portfolio.model.*;
import dev.portfolio.service.PortfolioService;

@Route(value = "about", layout = SiteLayout.class)
@PageTitle("About | Portfolio")
@AnonymousAllowed
public class AboutView extends Div {
    public AboutView(PortfolioService service) {
        addClassName("public-page");
        Profile p = service.profile();
        Div timeline = Ui.div("timeline");
        for (Experience e : service.experiences()) timeline.add(Ui.div("timeline-entry",
                Ui.span("eyebrow", e.getPeriod()), new H3(e.getRoleTitle()),
                Ui.span("org-label", e.getOrganization() + " · " + e.getLocation()), Ui.paragraph(e.getDescription())));
        Div skills = Ui.div("skill-grid");
        for (Skill s : service.skills()) {
            Div track = Ui.div("skill-progress");
            Div fill = Ui.div("skill-progress-fill");
            fill.getStyle().set("width", s.getProficiency() + "%");
            track.add(fill);
            skills.add(Ui.div("skill-meter", Ui.div("skill-meter-label",
                    new Span(s.getName()), Ui.span("eyebrow", s.getCategory())), track));
        }
        add(Ui.container(
            Ui.pageTitle("ABOUT / MY STORY", "More than code", p.getHeadline()),
            Ui.div("about-panel", Ui.span("eyebrow", "BACKGROUND"), Ui.paragraph(p.getAboutText()),
                Ui.span("eyebrow", p.getLocation()),
                Ui.external(p.getCvUrl(), "Download CV ↗", "text-link")),
            Ui.section(Ui.span("eyebrow", "EXPERIENCE"), Ui.heading("Where I've made an impact"), timeline),
            Ui.section(Ui.span("eyebrow", "CAPABILITIES"), Ui.heading("Skills & expertise"), skills),
            Ui.footer()));
    }
}
