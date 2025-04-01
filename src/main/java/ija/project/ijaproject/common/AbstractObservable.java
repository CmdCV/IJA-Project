package ija.project.ijaproject.common;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractObservable implements Observable {
    private final Set<Observer> observers = new HashSet<>();

    public AbstractObservable() {
    }

    public void addObserver(Observer o) {
        this.observers.add(o);
    }

    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    public void notifyObservers(String log) {
        this.observers.forEach((var1) -> var1.update(this, log));
    }
}
