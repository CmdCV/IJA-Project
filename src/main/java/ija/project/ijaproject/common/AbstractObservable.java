/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
# Abstract base for the Observer design pattern.        #
#                                                       #
# Adapted from assignment 2                             #
#########################################################
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
