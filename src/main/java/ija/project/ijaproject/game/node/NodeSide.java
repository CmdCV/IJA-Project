/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
# Enum representing the four sides (NORTH, EAST, SOUTH, #
# WEST) of a node in the game grid. Includes            #
# utility methods to get the next, previous and         #
# opposite directions for rotation logic.               #
#########################################################
*/

package ija.project.ijaproject.game.node;

/**
 * @brief Enum representing the sides of a node.
 *
 * Each side corresponds to a cardinal direction, and utility methods
 * allow for determining the next, previous, and opposite sides.
 */
public enum NodeSide {
    NORTH,  /**< The north side of the node. */
    EAST,   /**< The east side of the node. */
    SOUTH,  /**< The south side of the node. */
    WEST;   /**< The west side of the node. */

    /**
     * @brief Gets the next side in clockwise order.
     *
     * @return The next NodeSide in clockwise order.
     */
    public NodeSide next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * @brief Gets the previous side in counterclockwise order.
     *
     * @return The previous NodeSide in counterclockwise order.
     */
    public NodeSide previous() {
        return values()[(this.ordinal() - 1 + values().length) % values().length];
    }

    /**
     * @brief Gets the opposite side.
     *
     * @return The opposite NodeSide.
     */
    public NodeSide opposite() {
        return values()[(this.ordinal() + 2) % values().length];
    }
}
