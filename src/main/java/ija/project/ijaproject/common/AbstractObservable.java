/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
#  Abstract base for the Observer design pattern.       #
#  Allows registration, removal, and notification       #
#  of observers (Observer) within MVC architecture.     #
#  Intended for the model layer to inform multiple      #
#  system components about state changes.               #
#########################################################
*/




/**
 * @file AbstractObservable.java
 * @brief Abstract implementation of the Observable interface.
 * This class provides a base implementation of the Observable interface,
 * managing a set of observers and providing methods to add, remove, and notify them.
 * @note This code was adapted (stolen) from the second assignment.
 */

package ija.project.ijaproject.common;

import java.util.HashSet;
import java.util.Set;

/**
 * @brief Abstract class implementing the Observable interface.
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
     * @param o The observer to be added.
     * @brief Registers an observer to this observable object.
     */
    public void addObserver(Observer o) {
        this.observers.add(o);
    }

    /**
     * @param o The observer to be removed.
     * @brief Unregisters an observer from this observable object.
     */
    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    /**
     * @param log A message or data describing the event.
     * @brief Notifies all registered observers of an event.
     */
    public void notifyObservers(String log) {
        this.observers.forEach((var1) -> var1.update(this, log));
    }
}
