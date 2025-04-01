package ija.project.ijaproject.common;

public interface Observable {
    void addObserver(Observer o);

    void removeObserver(Observer o);

    void notifyObservers(String log);

    interface Observer {
        void update(Observable var1, String log);
    }
}
