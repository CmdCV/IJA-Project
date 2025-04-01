package ija.project.ijaproject.view;

import ija.project.ijaproject.common.GameNode;
import ija.project.ijaproject.common.tool.Observable;
import ija.project.ijaproject.common.tool.ToolField;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class FieldView extends Pane implements Observable.Observer {
    private final ToolField field;
    private final boolean infoView;
    private int changedModel = 0;
    private boolean initialLayoutDone = false;

    public FieldView(final ToolField field, boolean infoView) {
        this.field = field;
        this.infoView = infoView;
        this.setStyle("-fx-border-color: gray;");
        this.setPrefSize(50, 50);
        this.setMinSize(50, 50);  // Ensure minimum size
        if(!this.infoView){
            this.setOnMouseClicked(event -> field.turn());
        }
        field.addObserver(this);

        // Add a layout listener to update view once component is sized
        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!initialLayoutDone && newVal.doubleValue() > 0) {
                initialLayoutDone = true;
                updateView();
            }
        });
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        // Update view after initial layout if not already done
        if (!initialLayoutDone && getWidth() > 0) {
            initialLayoutDone = true;
            updateView();
        }
    }

    private void updateView() {
        this.getChildren().clear();
        double width = this.getWidth();
        double height = this.getHeight();

        // If not yet sized, use preferred size
        if (width <= 0) width = this.getPrefWidth();
        if (height <= 0) height = this.getPrefHeight();

        double centerX = width / 2;
        double centerY = height / 2;
        Color color = field.light() ? Color.RED : Color.BLACK;

        if (field.north()) {
            Line line = new Line(centerX, 0, centerX, centerY);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (field.east()) {
            Line line = new Line(width, centerY, centerX, centerY);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (field.south()) {
            Line line = new Line(centerX, height, centerX, centerY);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (field.west()) {
            Line line = new Line(0, centerY, centerX, centerY);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (field.isPower()) {
            this.setStyle("-fx-background-color: green; -fx-border-color: gray;");
        } else if (field.isBulb()) {
            Circle circle = new Circle(centerX, centerY, Math.min(width, height) / 2 - 5);
            circle.setFill(color);
            this.getChildren().add(circle);
        }

        // Add turn count information in info view mode
        if (infoView && field instanceof GameNode) {
            GameNode node = (GameNode) field;
            int turnsNeeded = node.turnsToInitialState();

            // Only show if turns are needed
            if (turnsNeeded > 0) {
                Label turnLabel = new Label(String.valueOf(turnsNeeded));
                turnLabel.setTextFill(Color.WHITE);
                turnLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                turnLabel.setTextAlignment(TextAlignment.CENTER);

                // Center the label
                turnLabel.setLayoutX(centerX - 5);
                turnLabel.setLayoutY(centerY - 10);

                // Add a background circle to make text more visible
                Circle background = new Circle(centerX, centerY, 12);
                background.setFill(Color.rgb(0, 0, 0, 0.7));

                this.getChildren().addAll(background, turnLabel);
            }
        }
    }

    @Override
    public void update(Observable observable, String event) {
        ++this.changedModel;
        Platform.runLater(this::updateView);
    }
}
