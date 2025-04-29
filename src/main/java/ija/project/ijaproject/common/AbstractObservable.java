/**
 * @file AbstractObservable.java
 * @brief Abstract implementation of the Observable interface.
 * 
 * This class provides a base implementation of the Observable interface,
 * managing a set of observers and providing methods to add, remove, and notify them.
 * 
 * @note This code was adapted (stolen) from the second assignment.
 */

package ija.project.ijaproject.common;

import java.util.HashSet;
import java.util.Set;

/**
 * @brief Abstract class implementing the Observable interface.
 * 
 * This class handles the registration, removal, and notification of observers.
 */
public abstract class AbstractObservable implements Observable {
    private final Set<Observer> observers = new HashSet<>();

    /**
     * @brief Default constructor.
     */
    public AbstractObservable() {
    }

    /**
     * @brief Registers an observer to this observable object.
     * 
     * @param o The observer to be added.
     */
    public void addObserver(Observer o) {
        this.observers.add(o);
    }

    /**
     * @brief Unregisters an observer from this observable object.
     * 
     * @param o The observer to be removed.
     */
    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    /**
     * @brief Notifies all registered observers of an event.
     * 
     * @param log A message or data describing the event.
     */
    public void notifyObservers(String log) {
        this.observers.forEach((var1) -> var1.update(this, log));
    }
}
