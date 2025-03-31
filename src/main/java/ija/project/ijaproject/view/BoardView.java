package ija.project.ijaproject.view;

import ija.project.ijaproject.common.tool.ToolEnvironment;
import ija.project.ijaproject.common.tool.ToolField;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class BoardView extends GridPane {
    private final ToolEnvironment environment;

    public BoardView(ToolEnvironment environment) {
        this.environment = environment;
        this.setHgap(2);
        this.setVgap(2);
        this.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setPadding(new Insets(2));

        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 1; row <= environment.rows(); row++) {
            for (int col = 1; col <= environment.cols(); col++) {
                ToolField field = environment.fieldAt(row, col);
                FieldView fieldView = new FieldView(field);
                this.add(fieldView, col-1, row-1);
            }
        }
    }
}
