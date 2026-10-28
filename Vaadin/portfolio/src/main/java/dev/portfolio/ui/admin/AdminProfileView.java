package dev.portfolio.ui.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.portfolio.model.Profile;
import dev.portfolio.service.PortfolioService;
import dev.portfolio.ui.Ui;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/profile", layout = AdminLayout.class)
@PageTitle("Profile | Portfolio CMS")
@RolesAllowed("ADMIN")
public class AdminProfileView extends Div {
    public AdminProfileView(PortfolioService service) {
        Profile profile = service.profile();
        TextField name = input("Full name", profile.getFullName(), 120);
        TextField headline = input("Professional headline", profile.getHeadline(), 220);
        TextField location = input("Location", profile.getLocation(), 120);
        EmailField email = new EmailField("Contact email"); email.setValue(profile.getEmail()); email.setRequired(true);
        email.setMaxLength(254);
        TextField availability = input("Availability status", profile.getAvailability(), 180);
        TextArea introduction = area("Short introduction", profile.getIntroduction(), 1400);
        TextArea about = area("About me", profile.getAboutText(), 10000);
        TextField github = input("GitHub URL", profile.getGithubUrl(), 500);
        TextField linkedin = input("LinkedIn URL", profile.getLinkedinUrl(), 500);
        TextField cv = input("CV URL", profile.getCvUrl(), 500);
        Button save = AdminUi.primary("Save profile", () -> {
            try {
                profile.setFullName(name.getValue()); profile.setHeadline(headline.getValue());
                profile.setLocation(location.getValue()); profile.setEmail(email.getValue());
                profile.setAvailability(availability.getValue()); profile.setIntroduction(introduction.getValue());
                profile.setAboutText(about.getValue()); profile.setGithubUrl(github.getValue());
                profile.setLinkedinUrl(linkedin.getValue()); profile.setCvUrl(cv.getValue());
                if (email.isInvalid()) throw new IllegalArgumentException("Enter a valid contact email");
                service.saveProfile(profile); AdminUi.success("Profile saved. Refresh the public site to see changes.");
            } catch (Exception ex) { AdminUi.fail(ex); }
        });
        add(AdminUi.page("Edit profile", "Change your public name, bio, social links and contact information.",
                Ui.div("admin-form", name, headline, location, email, availability,
                        introduction, about, github, linkedin, cv, save)));
    }
    private static TextField input(String label, String value, int max) {
        TextField field = new TextField(label); field.setWidthFull(); field.setMaxLength(max);
        field.setValue(value == null ? "" : value); return field;
    }
    private static TextArea area(String label, String value, int max) {
        TextArea field = new TextArea(label); field.setWidthFull(); field.setMaxLength(max);
        field.setMinHeight("120px"); field.setValue(value == null ? "" : value); return field;
    }
}
