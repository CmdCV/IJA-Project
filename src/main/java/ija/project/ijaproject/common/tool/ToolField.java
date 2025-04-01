//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ija.project.ijaproject.common.tool;

public interface ToolField extends Observable {
    void turn(boolean player);

    boolean north();

    boolean east();

    boolean south();

    boolean west();

    boolean light();

    boolean isLink();

    boolean isBulb();

    boolean isPower();
}
