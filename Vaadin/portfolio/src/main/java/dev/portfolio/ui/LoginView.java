package dev.portfolio.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Admin login | Portfolio")
@AnonymousAllowed
public class LoginView extends Div implements BeforeEnterObserver {
    private final LoginForm form = new LoginForm();
    public LoginView() {
        addClassName("login-page");
        form.setAction("login");
        add(Ui.div("login-panel", Ui.span("eyebrow", "SECURE PORTFOLIO CMS"),
                new H2("Welcome back"), Ui.paragraph("Sign in to manage your portfolio content."), form,
                Ui.link("/", "← Back to portfolio", "text-link")));
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            form.setError(true);
        }
    }
}
