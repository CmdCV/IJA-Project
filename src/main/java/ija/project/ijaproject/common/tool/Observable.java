//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ija.project.ijaproject.common.tool;

public interface Observable {
    void addObserver(Observer var1);

    void removeObserver(Observer var1);

    void notifyObservers(String log);

    public interface Observer {
        void update(Observable var1, String log);
    }
}
