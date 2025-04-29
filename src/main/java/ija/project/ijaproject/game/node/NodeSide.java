/**
 * @file NodeSide.java
 * @brief Represents the sides of a node in the game grid.
 * 
 * This enum defines the four cardinal directions (NORTH, EAST, SOUTH, WEST)
 * and provides utility methods for navigating between them.
 */

package ija.project.ijaproject.game.node;

/**
 * @brief Enum representing the sides of a node.
 * 
 * Each side corresponds to a cardinal direction, and utility methods
 * allow for determining the next, previous, and opposite sides.
 */
public enum NodeSide {
    NORTH, /**< The north side of the node. */
    EAST,  /**< The east side of the node. */
    SOUTH, /**< The south side of the node. */
    WEST;  /**< The west side of the node. */

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
