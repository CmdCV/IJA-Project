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
 * @file GameNode.java
 * @brief Represents a game node in the game grid.
 * This class extends AbstractObservable and represents a single node in the game grid.
 * It manages its type, position, sides, power state, and rotation logic.
 * @note This code was adapted (stolen) from the second assignment.
 */

package ija.project.ijaproject.game.node;

import ija.project.ijaproject.common.AbstractObservable;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ija.project.ijaproject.game.node.NodeSide.*;
import static ija.project.ijaproject.game.node.NodeType.EMPTY;
import static ija.project.ijaproject.game.node.NodeType.POWER;

/**
 * @brief Represents a single node in the game grid.
 *
 * A GameNode has a type, position, and sides that define its connections.
 * It can be rotated, powered, and observed for changes.
 */
public class GameNode extends AbstractObservable {
    private final NodeType type;
    /**< The type of the node. */
    private final NodePosition position;
    /**< The position of the node in the grid. */
    private final Set<NodeSide> initialSides;
    /**< The initial sides of the node. */
    private Set<NodeSide> sides;
    /**< The current sides of the node. */
    private int turnCount = 0;
    /**< The number of times the node has been rotated. */
    private boolean isPowered = false; /**< Whether the node is powered. */

    /**
     * @brief Constructs a GameNode with the given position, type, and sides.
     *
     * @param position The position of the node in the grid.
     * @param type The type of the node.
     * @param sides The sides of the node that are connected.
     */
    public GameNode(NodePosition position, NodeType type, NodeSide... sides) {
        this.type = type;
        this.position = position;
        this.sides = EnumSet.noneOf(NodeSide.class);
        Collections.addAll(this.sides, sides);
        this.initialSides = EnumSet.copyOf(this.sides);
        if (is(POWER)) this.isPowered = true;
    }

    /**
     * @brief Checks if the node connects to a specific side.
     *
     * @param s The side to check.
     * @return True if the node connects to the side, false otherwise.
     */
    public boolean connects(NodeSide s) {
        return this.sides.contains(s);
    }

    /**
     * @brief Checks if the node connects to all specified sides.
     *
     * @param s The sides to check.
     * @return True if the node connects to all sides, false otherwise.
     */
    public boolean connects(NodeSide... s) {
        for (NodeSide side : s) {
            if (!this.connects(side)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @brief Checks if the node is of a specific type.
     *
     * @param types The types to check.
     * @return True if the node matches any of the specified types, false otherwise.
     */
    public boolean is(NodeType... types) {
        return Arrays.asList(types).contains(this.type);
    }

    /**
     * @brief Sets the power state of the node.
     *
     * @param powered True to power the node, false to unpower it.
     */
    public void setPower(boolean powered) {
        if (this.isPowered != powered) {
            this.isPowered = powered;
            this.notifyObservers(null);
        }
    }

    /**
     * @brief Checks if the node is powered.
     *
     * @return True if the node is powered, false otherwise.
     */
    public boolean isPowered() {
        return this.isPowered;
    }

    /**
     * @brief Rotates the node clockwise.
     *
     * @param player True if the rotation is performed by a player, false otherwise.
     */
    public void turn(boolean player) {
        if (!this.is(EMPTY) && !this.connects(NORTH, EAST, SOUTH, WEST)) {
            Set<NodeSide> newSides = EnumSet.noneOf(NodeSide.class);
            for (NodeSide side : this.sides) {
                newSides.add(side.next());
            }
            this.sides = newSides;
            if (player) this.turnCount++;
            this.notifyObservers("T " + this.position);
        }
    }

    /**
     * @brief Rotates the node counterclockwise.
     *
     * @param player True if the rotation is performed by a player, false otherwise.
     */
    public void turnBack(boolean player) {
        if (!this.is(EMPTY)) {
            Set<NodeSide> newSides = EnumSet.noneOf(NodeSide.class);
            for (NodeSide side : this.sides) {
                newSides.add(side.previous());
            }
            this.sides = newSides;
            if (player) this.turnCount--;
            this.notifyObservers(null);
        }
    }

    /**
     * @brief Gets the number of times the node has been rotated.
     *
     * @return The rotation count.
     */
    public int turnCount() {
        return this.turnCount;
    }

    /**
     * @brief Calculates the number of rotations needed to return to the initial state.
     *
     * @return The number of rotations to return to the initial state.
     */
    public int turnsToInitialState() {
        Set<NodeSide> tempSides = EnumSet.copyOf(this.sides);
        for (int i = 0; i < 4; i++) {
            if (tempSides.containsAll(initialSides)) {
                return i;
            }

            // Rotate tempSides to check next position
            Set<NodeSide> rotatedSides = EnumSet.noneOf(NodeSide.class);
            for (NodeSide side : tempSides) {
                rotatedSides.add(side.next());
            }
            tempSides = rotatedSides;
        }

        // Should never reach here if sides are valid
        return 0;
    }

    /**
     * @brief Converts the node to a string representation.
     *
     * @return A string representing the node.
     */
    @Override
    public String toString() {
        final String connectorsStr = this.sides.stream()
                .map(NodeSide::name)
                .collect(Collectors.joining(","));
        return String.format("{%s%s[%s]}", this.type, this.position, connectorsStr);
    }
}
