/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
#                                                       #
#########################################################
*/

/**
 * @file GameLogger.java
 * @brief Handles logging of game actions and supports replay functionality.
 */

package ija.project.ijaproject.game;

import ija.project.ijaproject.common.Observable;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @class GameLogger
 * @brief Provides functionality for logging game actions, saving logs, and replaying actions.
 * The GameLogger class maintains a log of game actions, allows saving the log to a file,
 * and supports replaying actions by navigating through the log.
 */
public class GameLogger implements Observable.Observer {
    private final List<String> log = new ArrayList<>();  /// < List of logged actions.
    private int position = 0; /// < Current position in the log for replay.
    private boolean enabled = true; ///< Flag indicating whether logging is enabled.

    /**
     * @brief Default constructor for GameLogger.
     */
    public GameLogger() {
    }

    /**
     * @param var1  The observable object triggering the update.
     * @param event The event description.
     * @brief Updates the logger with a new event.
     */
    @Override
    public void update(Observable var1, String event) {
        if (event != null) logAction(event);
    }

    /**
     * @param description Description of the action to log.
     * @brief Logs a new action.
     */
    public void logAction(String description) {
        if (!enabled) return;
        // If we're in the middle of a replay, truncate future actions
        if (position < log.size() - 1) {
            log.subList(position + 1, log.size()).clear();
        }
        log.add(description);
        position = log.size() - 1;
    }

    /**
     * @brief Clears the log and resets the position.
     */
    public void clear() {
        this.log.clear();
        this.position = 0;
    }

    /**
     * @return The current log line as a string.
     * @brief Retrieves the current log line.
     */
    public String getLine() {
        return log.get(position);
    }

    /**
     * @brief Saves the log to a file.
     * Opens a file chooser dialog to select the save location and writes the log to the selected file.
     */
    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game Log");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Game Log Files", "*.log")
        );

        // Generate default filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("bulbGame_" + timestamp);

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

    /**
     * @return True if the position was moved, false otherwise.
     * @brief Moves to the previous log entry.
     */
    public boolean previous() {
        if (position > 0) {
            position--;
            return true;
        }
        return false;
    }

    /**
     * @return True if the position was moved, false otherwise.
     * @brief Moves to the next log entry.
     */
    public boolean next() {
        if (position < log.size() - 1) {
            position++;
            return true;
        }
        return false;
    }

    /**
     * @brief Disables logging.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * @brief Enables logging.
     */
    public void enable() {
        enabled = true;
    }
}
