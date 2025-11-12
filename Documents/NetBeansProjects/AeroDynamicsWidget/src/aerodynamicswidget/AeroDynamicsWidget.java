
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;

/**
 * AeroDynamicsWidget.java
 * A JavaFX prototype for Aero Dynamics' desktop weather widget.
 * - BorderPane layout
 * - Top: city header with search TextField and Refresh button
 * - Center: temperature, condition, animated compass and flight metrics
 * - Bottom: 3-day forecast
 * 
 * Styling is done via external CSS in "style.css" (same folder/resource path).
 */
public class AeroDynamicsWidget extends Application {

    private final Random random = new Random();

    private Label cityLabel;
    private TextField cityField;
    private Button refreshButton;

    private Label tempLabel;
    private Label condLabel;

    private Label windLabel;
    private Label visLabel;
    private Label flightCondLabel;
    private Rectangle flightCondIndicator;

    private RotateTransition needleRotation;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // TOP: Header
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // CENTER: Main weather + compass + metrics
        VBox center = createCenter();
        root.setCenter(center);

        // BOTTOM: 3-day forecast
        HBox bottom = createBottomForecast();
        root.setBottom(bottom);

        Scene scene = new Scene(root, 720, 480, Color.web("#FFFFFF"));

        // Load external CSS (style.css) from same folder or classpath
        URL cssUrl = getClass().getResource("/style.css");
        if (cssUrl == null) {
            // Try relative path (running from IDE or filesystem)
            try {
                cssUrl = new URL("file:" + System.getProperty("user.dir") + "/style.css");
            } catch (Exception ignored) {
            }
        }
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Warning: style.css not found on classpath or working directory. Make sure style.css is next to the compiled class or on the classpath.");
        }

        stage.setTitle("Aero Dynamics — Weather Widget Prototype");
        stage.setScene(scene);
        stage.show();

        // Initialize with a sample city
        cityField.setText("Seattle, WA");
        simulateRefresh();
    }

    private HBox createTopBar() {
        HBox top = new HBox(12);
        top.getStyleClass().add("top-bar");
        top.setPadding(new Insets(12));
        top.setAlignment(Pos.CENTER_LEFT);

        cityLabel = new Label("City:");
        cityLabel.getStyleClass().add("label-city");

        cityField = new TextField();
        cityField.setPromptText("Enter city (e.g. Seattle, WA)");
        cityField.getStyleClass().add("city-field");

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("refresh-button");

        // Bind the Refresh button disabled property to the TextField's emptiness
        refreshButton.disableProperty().bind(cityField.textProperty().isEmpty());

        refreshButton.setOnAction(e -> simulateRefresh());

        top.getChildren().addAll(cityLabel, cityField, refreshButton);
        return top;
    }


private VBox createCenter() {
        VBox center = new VBox(12);
        center.getStyleClass().add("center-pane");
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.CENTER);

        // Temperature and condition
        tempLabel = new Label("--°C");
        tempLabel.getStyleClass().add("temp-label");

        condLabel = new Label("--");
        condLabel.getStyleClass().add("cond-label");

        HBox mainRow = new HBox(24);
        mainRow.setAlignment(Pos.CENTER);

        // Compass group
        Group compass = createCompass();

        // Metrics card
        VBox metrics = createMetricsPanel();

        VBox tempBox = new VBox(8, tempLabel, condLabel);
        tempBox.setAlignment(Pos.CENTER);
        tempBox.getStyleClass().add("temp-box");

        mainRow.getChildren().addAll(tempBox, compass, metrics);

        center.getChildren().add(mainRow);
        return center;
    }

    private Group createCompass() {
        double size = 160;
        Circle face = new Circle(size / 2);
        face.getStyleClass().add("compass-face");

        // Needle: a red-orange line
        Line needle = new Line(0, 0, 0, -size / 2 + 10);
        needle.setStrokeWidth(4);
        needle.getStyleClass().add("compass-needle");

        Group g = new Group(face, needle);
        g.getStyleClass().add("compass-group");
        // center the group by translating it
        g.setTranslateX(size / 2);
        g.setTranslateY(size / 2);

        // Rotate pivot for the needle: rotate the needle around its center
        needle.setTranslateX(size / 2);
        needle.setTranslateY(size / 2);

        // Create a smooth rotate animation for the needle
        needleRotation = new RotateTransition(Duration.seconds(3), needle);
        needleRotation.setByAngle(360); // continuous spin to simulate heading
        needleRotation.setInterpolator(Interpolator.EASE_BOTH);
        needleRotation.setCycleCount(RotateTransition.INDEFINITE);
        needleRotation.play();

        return g;
    }

    private VBox createMetricsPanel() {
        VBox metrics = new VBox(8);
        metrics.getStyleClass().add("metrics-panel");
        metrics.setAlignment(Pos.CENTER_LEFT);

        windLabel = new Label("Wind: -- km/h");
        windLabel.getStyleClass().add("metric");

        visLabel = new Label("Visibility: -- km");
        visLabel.getStyleClass().add("metric");

        HBox flightRow = new HBox(8);
        flightRow.setAlignment(Pos.CENTER_LEFT);

        flightCondIndicator = new Rectangle(16, 16);
        flightCondIndicator.getStyleClass().add("flight-indicator");

        flightCondLabel = new Label("--");
        flightCondLabel.getStyleClass().add("flight-label");

        flightRow.getChildren().addAll(flightCondIndicator, flightCondLabel);

        metrics.getChildren().addAll(windLabel, visLabel, flightRow);
        return metrics;
    }

    private HBox createBottomForecast() {
        HBox bottom = new HBox(12);
        bottom.getStyleClass().add("bottom-forecast");
        bottom.setPadding(new Insets(12));
        bottom.setAlignment(Pos.CENTER);

        // 3 forecast day cards
        for (int i = 0; i < 3; i++) {
            VBox card = new VBox(6);
            card.getStyleClass().add("forecast-card");
            card.setAlignment(Pos.CENTER);
            Label day = new Label("Day " + (i + 1));
            day.getStyleClass().add("forecast-day");
            Label icon = new Label("☁️");
            icon.getStyleClass().add("forecast-icon");
            Label t = new Label("--°");
            t.getStyleClass().add("forecast-temp");
            card.getChildren().addAll(day, icon, t);
            bottom.getChildren().add(card);
        }

        return bottom;
    }

    private void simulateRefresh() {
        // Simulate fetching data and update UI with random values
        String city = cityField.getText().trim();
        if (city.isEmpty()) return;

        cityLabel.setText("City: " + city);

        
int temp = random.nextInt(35) - 5; // -5 to 29
        tempLabel.setText(String.format("%d°C", temp));

        String[] conds = {"Sunny", "Partly Cloudy", "Cloudy", "Rain", "Storm"};
        String cond = conds[random.nextInt(conds.length)];
        condLabel.setText(cond);

        int wind = 10 + random.nextInt(80);
        windLabel.setText(String.format("Wind: %d km/h", wind));

        int vis = 1 + random.nextInt(10);
        visLabel.setText(String.format("Visibility: %d km", vis));

        // Flight condition indicator: simple decision tree
        String flightCond;
        Color indicatorColor;
        if (vis >= 8 && wind < 25) {
            flightCond = "VFR";
            indicatorColor = Color.web("#00C853"); // green
        } else if (vis >= 4) {
            flightCond = "MVFR";
            indicatorColor = Color.web("#FFB300"); // amber
        } else {
            flightCond = "IFR";
            indicatorColor = Color.web("#D50000"); // red
        }
        flightCondLabel.setText(flightCond);
        flightCondIndicator.setFill(indicatorColor);

        // Update forecast temps
        HBox bottom = (HBox) ((BorderPane) cityLabel.getScene().getRoot()).getBottom();
        for (int i = 0; i < 3; i++) {
            VBox card = (VBox) bottom.getChildren().get(i);
            Label icon = (Label) card.getChildren().get(1);
            Label t = (Label) card.getChildren().get(2);
            int ft = temp + (i - 1) * (2 + random.nextInt(4));
            t.setText(String.format("%d°C", ft));
            // change icon mildly based on randomness
            String[] icons = {"☀️", "☁️", "🌧", "⛅️", "⚡️"};
            icon.setText(icons[random.nextInt(icons.length)]);
        }

        // Give needle a subtle heading animation: change rotation smoothly
        if (needleRotation != null) {
            // Restart with a slightly different speed to avoid perfect loops
            needleRotation.stop();
            double seconds = 2.5 + random.nextDouble() * 2.5; // 2.5 - 5s
            needleRotation.setDuration(Duration.seconds(seconds));
            needleRotation.playFromStart();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}