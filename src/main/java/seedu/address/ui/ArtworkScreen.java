package seedu.address.ui;

import javafx.animation.PauseTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * A short, undecorated screen showing a cyclist riding past a yellow sun with a caption.
 * It is used as the opening screen when the app starts and as the closing screen when it exits.
 */
public class ArtworkScreen {

    public static final String OPENING_CAPTION = "Every ride, organised.";
    public static final String CLOSING_CAPTION = "Ride safe. See you soon!";

    /** The sun uses the accent colour of the app theme (see {@code DarkTheme.css}). */
    private static final Color SUN_COLOR = Color.web("#ffd23f");
    private static final Color SKY_COLOR = Color.web("#20232b");
    private static final Color HILLS_COLOR = Color.web("#333947");
    private static final Color ROAD_COLOR = Color.web("#14161b");
    private static final Color ROAD_MARKING_COLOR = Color.web("#4a505e");
    private static final Color CYCLIST_COLOR = Color.web("#0b0c0f");
    private static final Color CAPTION_COLOR = Color.web("#c9ced8");

    /** The artwork is drawn on a 320 x 210 canvas and enlarged by this factor on screen. */
    private static final double CANVAS_WIDTH = 320;
    private static final double CANVAS_HEIGHT = 210;
    private static final double SCREEN_SCALE = 2;

    private final String caption;

    /**
     * Creates an {@code ArtworkScreen} that displays {@code caption} below the app name.
     */
    public ArtworkScreen(String caption) {
        this.caption = caption;
    }

    /**
     * Creates the screen shown when the app starts.
     */
    public static ArtworkScreen opening() {
        return new ArtworkScreen(OPENING_CAPTION);
    }

    /**
     * Creates the screen shown when the app exits.
     */
    public static ArtworkScreen closing() {
        return new ArtworkScreen(CLOSING_CAPTION);
    }

    /**
     * Shows this screen in its own window for {@code duration}. Once the time is up,
     * {@code onFinished} runs first and then the window closes. Running it first matters: JavaFX
     * exits the app when the last window closes, so the next window must already be showing.
     */
    public void showFor(Duration duration, Runnable onFinished) {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setTitle("Cyclique");
        stage.setScene(createScene());
        stage.centerOnScreen();
        stage.show();

        PauseTransition pause = new PauseTransition(duration);
        pause.setOnFinished(event -> {
            onFinished.run();
            stage.close();
        });
        pause.play();
    }

    /**
     * Returns a scene containing the artwork, enlarged to the size of the screen.
     */
    Scene createScene() {
        Group artwork = createArtwork();
        artwork.getTransforms().add(new Scale(SCREEN_SCALE, SCREEN_SCALE, 0, 0));
        return new Scene(new Group(artwork), CANVAS_WIDTH * SCREEN_SCALE, CANVAS_HEIGHT * SCREEN_SCALE);
    }

    private Group createArtwork() {
        Rectangle sky = new Rectangle(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        sky.setFill(SKY_COLOR);

        Circle sun = new Circle(205, 122, 62);
        sun.setFill(SUN_COLOR);

        SVGPath hills = new SVGPath();
        hills.setContent("M0 152Q70 112 140 142T320 132L320 210L0 210Z");
        hills.setFill(HILLS_COLOR);

        Rectangle road = new Rectangle(0, 172, CANVAS_WIDTH, 38);
        road.setFill(ROAD_COLOR);

        Line roadMarking = new Line(0, 192, CANVAS_WIDTH, 192);
        roadMarking.setStroke(ROAD_MARKING_COLOR);
        roadMarking.setStrokeWidth(2);
        roadMarking.getStrokeDashArray().addAll(14.0, 10.0);

        Group cyclist = createCyclist(CYCLIST_COLOR);
        cyclist.setLayoutX(112);
        cyclist.setLayoutY(81);
        cyclist.getTransforms().add(new Scale(0.95, 0.95, 0, 0));

        Text title = new Text(20, 52, "CYCLIQUE");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);

        Text captionText = new Text(20, 72, caption);
        captionText.setFont(Font.font("System", 12));
        captionText.setFill(CAPTION_COLOR);

        return new Group(sky, sun, hills, road, roadMarking, cyclist, title, captionText);
    }

    /**
     * Returns a side-on silhouette of a cyclist on a bike, drawn in {@code color}.
     */
    private static Group createCyclist(Color color) {
        Group cyclist = new Group();
        cyclist.getChildren().addAll(
                createWheel(40, 86, color),
                createWheel(148, 86, color),
                createLine("M40 86L82 86L72 50L124 52L82 86M124 52L148 86M72 50L40 86", color, 4),
                createLine("M66 44L78 44M124 52L128 43L141 46", color, 4),
                createLine("M74 42L112 26", color, 11),
                createLine("M112 26L138 46", color, 6),
                createLine("M74 42L100 64L88 90", color, 9));

        Circle head = new Circle(123, 16, 8);
        head.setFill(color);
        cyclist.getChildren().add(head);
        return cyclist;
    }

    private static Circle createWheel(double centreX, double centreY, Color color) {
        Circle wheel = new Circle(centreX, centreY, 27);
        wheel.setFill(null);
        wheel.setStroke(color);
        wheel.setStrokeWidth(4);
        return wheel;
    }

    /**
     * Returns an unfilled path with rounded ends, following the SVG path data in {@code content}.
     */
    private static SVGPath createLine(String content, Color color, double width) {
        SVGPath path = new SVGPath();
        path.setContent(content);
        path.setFill(null);
        path.setStroke(color);
        path.setStrokeWidth(width);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return path;
    }

}
