package dev.portfolio.ui.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.portfolio.model.Experience;
import dev.portfolio.service.PortfolioService;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/experience", layout = AdminLayout.class)
@PageTitle("Experience | Portfolio CMS")
@RolesAllowed("ADMIN")
public class AdminExperienceView extends Div {
    private final PortfolioService service;
    private final Grid<Experience> grid = new Grid<>(Experience.class, false);
    public AdminExperienceView(PortfolioService service) {
        this.service=service;
        grid.addColumn(Experience::getRoleTitle).setHeader("Role");
        grid.addColumn(Experience::getOrganization).setHeader("Organization");
        grid.addColumn(Experience::getPeriod).setHeader("Period");
        grid.addComponentColumn(e -> AdminUi.primary("Edit", () -> editor(e))).setHeader("Edit");
        grid.addComponentColumn(e -> AdminUi.danger("Delete", () -> delete(e))).setHeader("Delete");
        reload();
        add(AdminUi.page("Experience", "Your roles, organizations and achievements.",
                AdminUi.primary("+ Add experience", () -> editor(new Experience())), grid));
    }
    private void reload() { grid.setItems(service.experiences()); }
    private void delete(Experience e) {
        AdminUi.confirm("Delete experience at ‘" + e.getOrganization() + "’?", () -> {
            try { service.deleteExperience(e.getId()); reload(); AdminUi.success("Experience deleted"); }
            catch (Exception ex) { AdminUi.fail(ex); }
        });
    }
    private void editor(Experience e) {
        TextField role = AdminProjectsView.text("Role", e.getRoleTitle(), 160);
        TextField organization = AdminProjectsView.text("Organization", e.getOrganization(), 160);
        TextField period = AdminProjectsView.text("Period (e.g. 2023 — Present)", e.getPeriod(), 120);
        TextField location = AdminProjectsView.text("Location", e.getLocation(), 160);
        TextArea description = AdminProjectsView.area("Achievements and responsibilities", e.getDescription(), 10000);
        IntegerField order = new IntegerField("Display order"); order.setValue(e.getSortOrder());
        Dialog dialog = AdminUi.dialog(e.getId()==null?"Add experience":"Edit experience",
                role, organization, period, location, description, order);
        dialog.getFooter().add(new Button("Cancel",ev -> dialog.close()),
                AdminUi.primary("Save experience", () -> {
                    try {
                        e.setRoleTitle(role.getValue()); e.setOrganization(organization.getValue());
                        e.setPeriod(period.getValue()); e.setLocation(location.getValue());
                        e.setDescription(description.getValue()); e.setSortOrder(order.getValue()==null?0:order.getValue());
                        service.saveExperience(e); dialog.close(); reload(); AdminUi.success("Experience saved");
                    } catch (Exception ex) { AdminUi.fail(ex); }
                }));
        dialog.open();
    }
}
