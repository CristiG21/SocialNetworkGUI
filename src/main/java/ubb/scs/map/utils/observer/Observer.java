package ubb.scs.map.utils.observer;


import ubb.scs.map.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}