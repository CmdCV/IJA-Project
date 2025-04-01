package ija.project.ijaproject.view;

import ija.project.ijaproject.game.Game;
import ija.project.ijaproject.game.node.GameNode;
import ija.project.ijaproject.common.Observable;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import static ija.project.ijaproject.game.node.NodeSide.*;
import static ija.project.ijaproject.game.node.NodeType.*;

public class NodeView extends Pane implements Observable.Observer {
    private final GameNode node;
    private final boolean infoView;
    private final Game game;
    private boolean initialLayoutDone = false;

    public NodeView(final GameNode node, boolean infoView, int size, Game game) {
        this.node = node;
        this.infoView = infoView;
        this.game = game;
        this.setStyle("-fx-border-color: gray;");
        this.setPrefSize(size, size);
        this.setMinSize(size, size);  // Ensure minimum size

        if (!this.infoView) {
            this.setOnMouseClicked(event -> {
                if(!game.isComplete()) node.turn(true);
            });
        }
        // Add a layout listener to update view once component is sized
        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!initialLayoutDone && newVal.doubleValue() > 0) {
                initialLayoutDone = true;
                updateView();
            }
        });
        game.addObserver(this);
        node.addObserver(this);
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
        Color color = node.isPowered() ? Color.RED : Color.BLACK;

        if (node.connects(NORTH)) {
            Line line = new Line(centerX, 0, centerX, centerY);
            line.setStrokeWidth(height / 10);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (node.connects(EAST)) {
            Line line = new Line(width, centerY, centerX, centerY);
            line.setStrokeWidth(height / 10);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (node.connects(SOUTH)) {
            Line line = new Line(centerX, height, centerX, centerY);
            line.setStrokeWidth(height / 10);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (node.connects(WEST)) {
            Line line = new Line(0, centerY, centerX, centerY);
            line.setStrokeWidth(height / 10);
            line.setStroke(color);
            this.getChildren().add(line);
        }

        if (node.is(POWER)) {
            this.setStyle("-fx-background-color: green; -fx-border-color: gray;");
        } else if (node.is(BULB)) {
            Circle circle = new Circle(centerX, centerY, Math.min(width, height) / 2 - 5);
            circle.setFill(color);
            this.getChildren().add(circle);
        }

        if (infoView || game.isComplete()) {
            int turnCount = node.turnCount();
            int turnsToInitial = node.turnsToInitialState();

            // Display values depending on mode
            if (infoView && !node.is(EMPTY)) {
                // Create two labels for infoView mode
                Label totalLabel = new Label(String.valueOf(turnCount));
                totalLabel.setTextFill(Color.WHITE);
                totalLabel.setFont(Font.font("System", FontWeight.BOLD, height * 0.25));
                totalLabel.setTextAlignment(TextAlignment.CENTER);

                Label neededLabel = new Label("/" + turnsToInitial);
                neededLabel.setTextFill(Color.LIGHTBLUE);
                neededLabel.setFont(Font.font("System", FontWeight.BOLD, height * 0.25));
                neededLabel.setTextAlignment(TextAlignment.CENTER);

                // Position the labels
                totalLabel.setLayoutX(centerX - (height * 0.2));
                totalLabel.setLayoutY(centerY - (height * 0.15));

                neededLabel.setLayoutX(centerX);
                neededLabel.setLayoutY(centerY - (height * 0.15));

                // Add a background circle
                Circle background = new Circle(centerX, centerY, height * 0.3);
                background.setFill(Color.rgb(0, 0, 0, 0.7));

                this.getChildren().addAll(background, totalLabel, neededLabel);
            }
            // Display only turnCount when game is complete
            else if (game.isComplete() && turnCount > 0) {
                Label turnLabel = new Label(String.valueOf(turnCount));
                turnLabel.setTextFill(Color.WHITE);
                turnLabel.setFont(Font.font("System", FontWeight.BOLD, height * 0.33));
                turnLabel.setTextAlignment(TextAlignment.CENTER);

                // Center the label
                turnLabel.setLayoutX(centerX - (height * 0.1));
                turnLabel.setLayoutY(centerY - (height * 0.2));

                // Add a background circle
                Circle background = new Circle(centerX, centerY, height * 0.25);
                background.setFill(Color.rgb(0, 0, 0, 0.7));

                this.getChildren().addAll(background, turnLabel);
            }
        }
    }

    @Override
    public void update(Observable observable, String event) {
        Platform.runLater(this::updateView);
    }
}
