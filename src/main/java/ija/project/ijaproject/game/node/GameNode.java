package ija.project.ijaproject.game.node;

import ija.project.ijaproject.common.AbstractObservable;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ija.project.ijaproject.game.node.NodeSide.*;
import static ija.project.ijaproject.game.node.NodeType.*;

public class GameNode extends AbstractObservable {
    private final NodeType type;
    private final NodePosition position;
    private final Set<NodeSide> initialSides;
    private Set<NodeSide> sides;
    private int turnCount = 0;
    private boolean isPowered = false;

    public GameNode(NodePosition position, NodeType type, NodeSide... sides) {
        this.type = type;
        this.position = position;
        this.sides = EnumSet.noneOf(NodeSide.class);
        Collections.addAll(this.sides, sides);
        this.initialSides = EnumSet.copyOf(this.sides);
        if (is(POWER)) this.isPowered = true;
    }

    public boolean connects(NodeSide s) {
        return this.sides.contains(s);
    }
    public boolean connects(NodeSide... s) {
        for (NodeSide side : s) {
            if (!this.connects(side)) {
                return false;
            }
        }
        return true;
    }

    public boolean is(NodeType... types) {
        return Arrays.asList(types).contains(this.type);
    }

    public void setPower(boolean powered) {
        if (this.isPowered != powered) {
            this.isPowered = powered;
            this.notifyObservers(null);
        }
    }

    public boolean isPowered() {
        return this.isPowered;
    }

    public void turn(boolean player) {
        if(!this.is(EMPTY) && !this.connects(NORTH, EAST, SOUTH, WEST)) {
            Set<NodeSide> newSides = EnumSet.noneOf(NodeSide.class);
            for (NodeSide side : this.sides) {
                newSides.add(side.next());
            }
            this.sides = newSides;
            if (player) this.turnCount++;
            this.notifyObservers("T " + this.position);
        }
    }

    public void turnBack(boolean player) {
        if(!this.is(EMPTY)) {
            Set<NodeSide> newSides = EnumSet.noneOf(NodeSide.class);
            for (NodeSide side : this.sides) {
                newSides.add(side.previous());
            }
            this.sides = newSides;
            if (player) this.turnCount--;
            this.notifyObservers(null);
        }
    }

    public int turnCount() {
        return this.turnCount;
    }

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

    @Override
    public String toString() {
        final String connectorsStr = this.sides.stream()
                .map(NodeSide::name)
                .collect(Collectors.joining(","));
        return String.format("{%s%s[%s]}", this.type, this.position, connectorsStr);
    }
}
