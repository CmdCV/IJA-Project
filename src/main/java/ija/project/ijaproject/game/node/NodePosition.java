/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
# Represents a node's position in the grid.             #
# Stores row and column values, and provides utilities  #
# to parse from string and convert to formatted string. #
#########################################################
*/

package ija.project.ijaproject.game.node;

/**
 * @brief Represents the position of a node in the game grid.
 *
 * A NodePosition consists of a row and a column, and provides methods
 * for creating a position from a string and converting it to a string.
 */
public record NodePosition(int row, int col) {

    /**
     * @brief Creates a NodePosition from a string representation.
     *
     * The string should be in the format "[row@col]".
     *
     * @param str The string representation of the position.
     * @return A NodePosition object, or null if the string is invalid.
     */
    public static NodePosition fromString(String str) {
        try {
            String[] coords = str.replace("[", "").replace("]", "").split("@");
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);
            return new NodePosition(row, col);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @brief Converts the NodePosition to a string representation.
     *
     * The format of the string is "[row@col]".
     *
     * @return A string representing the position.
     */
    @Override
    public String toString() {
        return String.format("[%d@%d]", this.row(), this.col());
    }

}
