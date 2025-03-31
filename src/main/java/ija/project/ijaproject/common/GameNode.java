package ija.project.ijaproject.common;

import ija.project.ijaproject.common.tool.AbstractObservableField;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GameNode extends AbstractObservableField {
    private final Type type;
    private final Position position;
    private Set<Side> sides;
    private boolean isPowered = false;

    public GameNode(Position position, Type type, Side... sides) {
        this.type = type;
        this.position = position;
        this.sides = EnumSet.noneOf(Side.class);
        Collections.addAll(this.sides, sides);
    }

    public boolean containsConnector(Side s) {
        return this.sides.contains(s);
    }

    public Position getPosition() {
        return this.position;
    }

    public boolean isLink() {
        return this.type == Type.LINK;
    }

    public boolean isBulb() {
        return this.type == Type.BULB;
    }

    public boolean isPower() {
        return this.type == Type.POWER;
    }

    public boolean light() {
        return this.isPowered;
    }

    public void setPowered(boolean powered) {
        if (this.isPowered != powered) {
            this.isPowered = powered;
            this.notifyObservers();
        }
    }

    public void turn() {
        Set<Side> newSides = EnumSet.noneOf(Side.class);
        for (Side side : this.sides) {
            newSides.add(side.next());
        }
        this.sides = newSides;
        this.notifyObservers();
    }

    @Override
    public boolean north() {
        return this.containsConnector(Side.NORTH);
    }

    @Override
    public boolean east() {
        return this.containsConnector(Side.EAST);
    }

    @Override
    public boolean south() {
        return this.containsConnector(Side.SOUTH);
    }

    @Override
    public boolean west() {
        return this.containsConnector(Side.WEST);
    }

    @Override
    public String toString() {
        final String connectorsStr = this.sides.stream()
                .map(Side::name)
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
        final boolean NORTH = this.containsConnector(Side.NORTH);
        final boolean EAST = this.containsConnector(Side.EAST);
        final boolean SOUTH = this.containsConnector(Side.SOUTH);
        final boolean WEST = this.containsConnector(Side.WEST);

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
