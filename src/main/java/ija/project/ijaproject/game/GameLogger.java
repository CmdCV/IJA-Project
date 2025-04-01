package ija.project.ijaproject.game;

import ija.project.ijaproject.common.tool.Observable;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameLogger implements Observable.Observer {
    private List<String> log = new ArrayList<>();
    private int position = 0;
    private boolean enabled = true;

    public GameLogger() {
    }

    @Override
    public void update(Observable var1, String event) {
        if (event != null) logAction(event);
    }

    public void logAction(String description) {
        if (!enabled) return;
        // If we're in the middle of a replay, truncate future actions
        if (position < log.size() - 1) {
            log.subList(position + 1, log.size()).clear();
        }
        log.add(description);
        position = log.size() - 1;
    }

    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game Log");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Game Log Files", "*.glog")
        );

        // Generate default filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("game_" + timestamp + ".glog");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String action : this.log) {
                    writer.println(action);
                }
            } catch (IOException e) {
                System.err.println("Error saving game log: " + e.getMessage());
            }
        }
    }

    public void next() {
        if (position < log.size() - 1) {
            position++;
        }
    }

    public void previous() {
        if (position > 0) {
            position--;
        }
    }

    public int position() {
        return position;
    }

    public List<String> log() {
        return log;
    }

    public String getLine() {
        return log.get(position);
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }


    public void clear() {
        this.log.clear();
    }
}
