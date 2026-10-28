package com.portfolio.desktop;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class DesktopApp extends Application {
    private PortfolioApiClient client;
    private JsonNode portfolio;
    private final BorderPane layout = new BorderPane();
    private final StackPane content = new StackPane();
    private final Label statusLabel = new Label("Connecting to server...");
    private final Map<String, Button> navigation = new LinkedHashMap<>();
    private String currentSection = "Overview";

    @Override
    public void start(Stage stage) {
        String endpoint = System.getenv().getOrDefault("PORTFOLIO_API_URL", "http://localhost:8080");
        client = new PortfolioApiClient(endpoint);
        layout.getStyleClass().add("app-root");
        layout.setLeft(createSidebar());
        layout.setCenter(content);
        content.getChildren().setAll(label("Loading your portfolio...", "page-subtitle"));
        var scene = new Scene(layout, 1200, 790);
        scene.getStylesheets().add(getClass().getResource("/desktop.css").toExternalForm());
        stage.setTitle("Portfolio • JavaFX Desktop Client");
        stage.setMinWidth(780);
        stage.setMinHeight(550);
        stage.setScene(scene);
        stage.show();
        loadPortfolio();
    }

    private VBox createSidebar() {
        var sidebar = new VBox(14);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(235);
        var logo = label("✦  PORTFOLIO", "logo");
        var sub = label("JAVA ENGINEER", "micro-label");
        sidebar.getChildren().addAll(logo, sub);
        var spacer = new Region(); spacer.setMinHeight(23);
        sidebar.getChildren().add(spacer);
        for (String item : Arrays.asList("Overview", "Projects", "Skills", "Experience", "Contact")) {
            Button button = new Button(switch (item) {
                case "Overview" -> "⌂    Overview";
                case "Projects" -> "▦    Projects";
                case "Skills" -> "⬡    Skills";
                case "Experience" -> "◷    Experience";
                default -> "✉    Contact";
            });
            button.getStyleClass().add("nav-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(e -> show(item));
            navigation.put(item, button);
            sidebar.getChildren().add(button);
        }
        var grow = new Region(); VBox.setVgrow(grow, Priority.ALWAYS);
        sidebar.getChildren().add(grow);
        statusLabel.getStyleClass().add("sidebar-status"); statusLabel.setWrapText(true);
        var refresh = new Button("↻   Refresh data");
        refresh.getStyleClass().add("refresh-button");
        refresh.setMaxWidth(Double.MAX_VALUE);
        refresh.setOnAction(e -> loadPortfolio());
        sidebar.getChildren().addAll(statusLabel, refresh, label("API  " + endpointName(), "sidebar-footnote"));
        return sidebar;
    }

    private String endpointName() { return client.getBaseUrl(); }

    private void loadPortfolio() {
        statusLabel.setText("Connecting...");
        client.getPortfolio().whenComplete((data, error) -> Platform.runLater(() -> {
            if (error != null) {
                statusLabel.setText("● API offline");
                var retry = new Button("Retry connection"); retry.getStyleClass().add("primary-button");
                retry.setOnAction(e -> loadPortfolio());
                content.getChildren().setAll(vertical(
                        label("Couldn't reach the portfolio API", "page-title"),
                        label("Start Spring Boot or set PORTFOLIO_API_URL.", "page-subtitle"), retry));
                return;
            }
            portfolio = data;
            statusLabel.setText("● API connected");
            show(currentSection);
        }));
    }

    private void show(String section) {
        currentSection = section;
        navigation.forEach((name, button) -> {
            button.getStyleClass().remove("nav-active");
            if (name.equals(section)) button.getStyleClass().add("nav-active");
        });
        if (portfolio == null) return;
        Parent inner = switch (section) {
            case "Projects" -> projects();
            case "Skills" -> skills();
            case "Experience" -> experience();
            case "Contact" -> contact();
            default -> overview();
        };
        ScrollPane scroll = new ScrollPane(inner);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("main-scroll");
        content.getChildren().setAll(scroll);
    }

    private VBox page(String kicker, String heading, String subtitle) {
        var page = new VBox(22);
        page.getStyleClass().add("page");
        page.getChildren().addAll(label(kicker, "kicker"), label(heading, "page-title"), label(subtitle, "page-subtitle"));
        return page;
    }

    private Parent overview() {
        JsonNode p = portfolio.path("profile");
        VBox page = page("WELCOME / ABOUT", p.path("headline").asText(), p.path("summary").asText());
        page.getChildren().add(label("HEY, I'M " + p.path("name").asText().toUpperCase(), "kicker"));
        var stats = new HBox(15);
        for (JsonNode stat : p.path("stats")) {
            var card = vertical(label(stat.path("value").asText(), "stat-value"), label(stat.path("label").asText(), "card-subtitle"));
            card.getStyleClass().add("card");
            HBox.setHgrow(card, Priority.ALWAYS);
            card.setMaxWidth(Double.MAX_VALUE);
            stats.getChildren().add(card);
        }
        page.getChildren().add(stats);
        page.getChildren().add(label("FOCUS", "kicker"));
        var card = vertical(label(p.path("role").asText(), "card-title"),
                label("Location  ·  " + p.path("location").asText(), "card-subtitle"),
                label("Let's create scalable and human-friendly experiences.", "card-description"));
        card.getStyleClass().add("card"); page.getChildren().add(card);
        var links = new HBox(10);
        for (JsonNode social : p.path("social")) {
            String url = social.path("url").asText();
            var button = new Button(social.path("label").asText() + " ↗"); button.getStyleClass().add("secondary-button");
            button.setOnAction(e -> openWeb(url)); links.getChildren().add(button);
        }
        var projects = new Button("View selected projects  →"); projects.getStyleClass().add("primary-button");
        projects.setOnAction(e -> show("Projects")); links.getChildren().add(projects);
        page.getChildren().add(links);
        return page;
    }

    private Parent projects() {
        VBox page = page("01 / WORK", "Selected projects", "Practical software built with care and intention.");
        for (JsonNode project : portfolio.path("projects")) {
            var card = new VBox(12); card.getStyleClass().add("card");
            card.getChildren().addAll(label("PROJECT " + project.path("id").asText() + "   /   " + project.path("category").asText(), "kicker"),
                    label(project.path("title").asText(), "card-title"),
                    label(project.path("description").asText(), "card-description"));
            var tags = new FlowPane(8, 8);
            for (JsonNode s : project.path("stack")) { Label tag = label(s.asText(), "tag"); tags.getChildren().add(tag); }
            card.getChildren().add(tags);
            var linkBar = new HBox(10);
            for (String key : new String[] {"demoUrl", "sourceUrl"}) {
                String url = project.path(key).asText();
                if (!url.isBlank()) {
                    var b = new Button(key.equals("demoUrl") ? "Live demo ↗" : "Source code ↗");
                    b.getStyleClass().add("secondary-button"); b.setOnAction(e -> openWeb(url)); linkBar.getChildren().add(b);
                }
            }
            card.getChildren().add(linkBar);
            page.getChildren().add(card);
        }
        return page;
    }

    private Parent skills() {
        VBox page = page("02 / STACK", "Tools of the trade", "Technology that makes ideas useful.");
        for (JsonNode skill : portfolio.path("skills")) {
            var card = vertical(label(skill.path("group").asText(), "card-title")); card.getStyleClass().add("card");
            var tags = new FlowPane(10, 10);
            for (JsonNode item : skill.path("items")) tags.getChildren().add(label(item.asText(), "tag"));
            card.getChildren().add(tags); page.getChildren().add(card);
        }
        return page;
    }

    private Parent experience() {
        VBox page = page("03 / JOURNEY", "Experience & growth", "Where curiosity meets delivery.");
        for (JsonNode item : portfolio.path("experience")) {
            var card = vertical(label(item.path("period").asText(), "kicker"),
                    label(item.path("role").asText(), "card-title"),
                    label(item.path("company").asText(), "card-subtitle"),
                    label(item.path("description").asText(), "card-description"));
            card.getStyleClass().add("card"); page.getChildren().add(card);
        }
        return page;
    }

    private Parent contact() {
        VBox page = page("04 / CONTACT", "Let's build something", "Send a message directly to the Spring Boot backend.");
        var form = new VBox(12); form.getStyleClass().add("card");
        TextField name = new TextField(); name.setPromptText("Your name (max 100 characters)");
        TextField email = new TextField(); email.setPromptText("Your email address");
        TextField subject = new TextField(); subject.setPromptText("Subject");
        TextArea message = new TextArea(); message.setPromptText("Tell me about your idea...");
        message.setPrefRowCount(5); message.setWrapText(true);
        Label feedback = label("", "card-subtitle"); feedback.setWrapText(true);
        Button send = new Button("Send message  ↗"); send.getStyleClass().add("primary-button");
        send.setOnAction(e -> {
            String n = name.getText().trim(), em = email.getText().trim(), s = subject.getText().trim(), m = message.getText().trim();
            if (n.isBlank() || n.length() > 100 || em.length() > 254 || !em.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") ||
                    s.isBlank() || s.length() > 160 || m.length() < 10 || m.length() > 3000) {
                feedback.setText("Please enter a valid name, email, subject and message (10–3000 characters)."); return;
            }
            send.setDisable(true); feedback.setText("Sending message...");
            client.sendContact(n, em, s, m).whenComplete((ignored, error) -> Platform.runLater(() -> {
                send.setDisable(false);
                if (error != null) feedback.setText("Unable to send: " + rootMessage(error));
                else { feedback.setText("✓ Message sent successfully!"); name.clear(); email.clear(); subject.clear(); message.clear(); }
            }));
        });
        form.getChildren().addAll(label("YOUR NAME", "form-label"), name, label("EMAIL", "form-label"), email,
                label("SUBJECT", "form-label"), subject, label("MESSAGE", "form-label"), message, send, feedback);
        page.getChildren().add(form);
        return page;
    }

    private String rootMessage(Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() == null ? "Server error" : cause.getMessage();
    }

    private void openWeb(String url) {
        try {
            URI uri = URI.create(url);
            if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
                getHostServices().showDocument(uri.toString());
            }
        } catch (IllegalArgumentException ignored) { /* malformed link in profile data */ }
    }

    private Label label(String value, String styleClass) {
        var result = new Label(value);
        result.getStyleClass().add(styleClass);
        result.setWrapText(true);
        result.setMaxWidth(Double.MAX_VALUE);
        return result;
    }

    private VBox vertical(Node... nodes) {
        var box = new VBox(13);
        box.getChildren().addAll(nodes);
        return box;
    }

    public static void main(String[] args) { launch(args); }
}
