package ija.project.ijaproject.view;

import ija.project.ijaproject.game.Game;
import ija.project.ijaproject.game.node.GameNode;
import ija.project.ijaproject.game.node.NodePosition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BoardView extends GridPane {
    private final Game game;
    private final boolean infoBoardView;

    public BoardView(final Game game, boolean infoBoardView) {
        this.game = game;
        this.infoBoardView = infoBoardView;
        this.setHgap(2);
        this.setVgap(2);
        this.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setPadding(new Insets(2));

        // Center the grid within its parent
        this.setAlignment(Pos.CENTER);

        // Allow the grid to grow if needed
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        initializeBoard();
    }

    private void initializeBoard() {
        int size = 500 / Math.max(game.rows(), game.cols());
        for (int row = 1; row <= game.rows(); row++) {
            for (int col = 1; col <= game.cols(); col++) {
                GameNode field = game.node(new NodePosition(row, col));
                NodeView nodeView = new NodeView(field, this.infoBoardView, size, game);
                this.add(nodeView, col - 1, row - 1);
            }
        }
    }
}
