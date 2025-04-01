package ija.project.ijaproject.common;

import ija.project.ijaproject.common.tool.AbstractObservableField;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GameNode extends AbstractObservableField {
    private final NodeType type;
    private final NodePosition position;
    private Set<NodeSide> sides;
    private final Set<NodeSide> initialSides;
    private int turnCount = 0;
    private boolean isPowered = false;

    public GameNode(NodePosition position, NodeType type, NodeSide... sides) {
        this.type = type;
        this.position = position;
        this.sides = EnumSet.noneOf(NodeSide.class);
        Collections.addAll(this.sides, sides);
        this.initialSides = EnumSet.copyOf(this.sides);
        this.turnCount = 0;
    }

    public boolean containsConnector(NodeSide s) {
        return this.sides.contains(s);
    }

    public NodePosition getPosition() {
        return this.position;
    }

    public boolean isLink() {
        return this.type == NodeType.LINK;
    }

    public boolean isBulb() {
        return this.type == NodeType.BULB;
    }

    public boolean isPower() {
        return this.type == NodeType.POWER;
    }

    public boolean isEmpty() {
        return this.type == NodeType.EMPTY;
    }
    public boolean light() {
        return this.isPowered;
    }

    public void setPowered(boolean powered) {
        if (this.isPowered != powered) {
            this.isPowered = powered;
            this.notifyObservers(null);
        }
    }

    public void turn() {
        Set<NodeSide> newSides = EnumSet.noneOf(NodeSide.class);
        for (NodeSide side : this.sides) {
            newSides.add(side.next());
        }
        this.sides = newSides;
        this.turnCount++;
        this.notifyObservers("T " + this.position);
    }

    public void turnBack() {
        Set<NodeSide> newSides = EnumSet.noneOf(NodeSide.class);
        for (NodeSide side : this.sides) {
            newSides.add(side.previous());
        }
        this.sides = newSides;
        this.turnCount--;
        this.notifyObservers(null);
    }

    public int turnsToInitialState() {
        // Compare current sides with initial sides
        // For each possible rotation (0-3), check if rotating would match initial state

        Set<NodeSide> tempSides = EnumSet.copyOf(this.sides);
        for (int i = 0; i < 4; i++) {
            if (sidesEquivalent(tempSides, initialSides)) {
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

    private boolean sidesEquivalent(Set<NodeSide> sides1, Set<NodeSide> sides2) {
        if (sides1.size() != sides2.size()) {
            return false;
        }
        return sides1.containsAll(sides2);
    }

    @Override
    public boolean north() {
        return this.containsConnector(NodeSide.NORTH);
    }

    @Override
    public boolean east() {
        return this.containsConnector(NodeSide.EAST);
    }

    @Override
    public boolean south() {
        return this.containsConnector(NodeSide.SOUTH);
    }

    @Override
    public boolean west() {
        return this.containsConnector(NodeSide.WEST);
    }

    @Override
    public String toString() {
        final String connectorsStr = this.sides.stream()
                .map(NodeSide::name)
                .collect(Collectors.joining(","));
        return String.format("{%s%s[%s]}", this.type, this.position, connectorsStr);
    }

    private String getNodeColor() {
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";

        if (this.isPower()) {
            return RED;
        } else if ((this.isLink() || this.isBulb()) && this.light()) {
            return YELLOW;
        }
        return RESET;
    }

    public String getNodeSymbol() {
        final boolean NORTH = this.containsConnector(NodeSide.NORTH);
        final boolean EAST = this.containsConnector(NodeSide.EAST);
        final boolean SOUTH = this.containsConnector(NodeSide.SOUTH);
        final boolean WEST = this.containsConnector(NodeSide.WEST);

        char symbol = ' ';
        if (this.isBulb()) {
            if (NORTH) symbol = 'v';
            else if (EAST) symbol = '<';
            else if (SOUTH) symbol = '^';
            else if (WEST) symbol = '>';
        } else if (this.isLink() || this.isPower()) {
            if (NORTH && EAST && SOUTH && WEST) symbol = '╬';
            else if (NORTH && EAST && SOUTH) symbol = '╠';
            else if (NORTH && EAST && WEST) symbol = '╩';
            else if (NORTH && SOUTH && WEST) symbol = '╣';
            else if (EAST && SOUTH && WEST) symbol = '╦';
            else if (NORTH && EAST) symbol = '╚';
            else if (NORTH && WEST) symbol = '╝';
            else if (SOUTH && EAST) symbol = '╔';
            else if (SOUTH && WEST) symbol = '╗';
            else if (NORTH && SOUTH) symbol = '║';
            else if (EAST && WEST) symbol = '═';
            else if (NORTH) symbol = '╹';
            else if (SOUTH) symbol = '╻';
            else if (EAST) symbol = '╺';
            else if (WEST) symbol = '╸';
        }

        final String COLOR = this.getNodeColor();
        final String RESET = "\u001B[0m";
        return COLOR + symbol + RESET;
    }
}
