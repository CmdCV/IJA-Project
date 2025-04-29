/**
 * @file Observable.java
 * @brief Interface for implementing the Observer design pattern.
 * 
 * This interface allows objects to be observed by other objects (observers).
 * Observers can subscribe to an Observable to receive updates when certain events occur.
 * 
 * @note This code was adapted (stolen) from the second assignment.
 */

package ija.project.ijaproject.common;

/**
 * @brief Interface representing an observable object.
 * 
 * Classes implementing this interface allow observers to register, unregister,
 * and receive notifications about events.
 */
public interface Observable {

    /**
     * @brief Registers an observer to this observable object.
     * 
     * @param o The observer to be added.
     */
    void addObserver(Observer o);

    /**
     * @brief Unregisters an observer from this observable object.
     * 
     * @param o The observer to be removed.
     */
    void removeObserver(Observer o);

    /**
     * @brief Notifies all registered observers of an event.
     * 
     * @param log A message or data describing the event.
     */
    void notifyObservers(String log);

    /**
     * @brief Interface for objects that want to observe an Observable.
     * 
     * Classes implementing this interface can receive updates from an Observable.
     */
    interface Observer {

        /**
         * @brief Called when the observable object notifies its observers.
         * 
         * @param var1 The observable object that triggered the update.
         * @param log A message or data describing the event.
         */
        void update(Observable var1, String log);
    }
}
