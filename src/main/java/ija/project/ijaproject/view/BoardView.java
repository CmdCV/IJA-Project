package ija.project.ijaproject.view;

import ija.project.ijaproject.common.tool.ToolEnvironment;
import ija.project.ijaproject.common.tool.ToolField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BoardView extends GridPane {
    private final ToolEnvironment environment;
    private final boolean infoBoardView;

    public BoardView(ToolEnvironment environment, boolean infoBoardView) {
        this.environment = environment;
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
        int size = 500 / Math.max(environment.rows(), environment.cols());
        for (int row = 1; row <= environment.rows(); row++) {
            for (int col = 1; col <= environment.cols(); col++) {
                ToolField field = environment.fieldAt(row, col);
                FieldView fieldView = new FieldView(field, this.infoBoardView, size);
                this.add(fieldView, col - 1, row - 1);
            }
        }
    }
}
