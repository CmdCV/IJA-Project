//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ija.project.ijaproject.common.tool;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractObservable implements Observable {
    private final Set<Observer> observers = new HashSet();

    public AbstractObservable() {
    }

    public void addObserver(Observer var1) {
        this.observers.add(var1);
    }

    public void removeObserver(Observer var1) {
        this.observers.remove(var1);
    }

    public void notifyObservers(String log) {
        this.observers.forEach((var1) -> var1.update(this, log));
    }
}
