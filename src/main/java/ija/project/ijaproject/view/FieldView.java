package ija.project.ijaproject.view;

import ija.project.ijaproject.common.tool.Observable;
import ija.project.ijaproject.common.tool.ToolField;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class FieldView extends Pane implements Observable.Observer {
    private final ToolField field;
    private int changedModel = 0;
    private boolean initialLayoutDone = false;

    public FieldView(final ToolField field) {
        this.field = field;
        this.setStyle("-fx-border-color: gray;");
        this.setPrefSize(50, 50);
        this.setMinSize(50, 50);  // Ensure minimum size
        this.setOnMouseClicked(event -> field.turn());
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
    }

    @Override
    public void update(Observable observable) {
        ++this.changedModel;
        Platform.runLater(this::updateView);
    }
}
