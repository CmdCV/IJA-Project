/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
# Enum representing node types used in the game grid.   #
# Defines BULB, LINK, POWER, and EMPTY node types.      #
#########################################################
*/

package ija.project.ijaproject.game.node;

/**
 * @brief Enum representing the types of nodes in the game grid.
 *
 * Each node type has a specific role in the game, such as being a bulb, a link,
 * a power source, or an empty node.
 */
public enum NodeType {
    BULB, /**< Represents a bulb node. */
    LINK, /**< Represents a link node. */
    POWER, /**< Represents a power source node. */
    EMPTY; /**< Represents an empty node. */

    /**
     * @brief Converts the NodeType to its string representation.
     *
     * @return A string representing the node type.
     */
    @Override
    public String toString() {
        return switch (this) {
            case BULB -> "B";
            case LINK -> "L";
            case POWER -> "P";
            case EMPTY -> "E";
        };
    }
}
