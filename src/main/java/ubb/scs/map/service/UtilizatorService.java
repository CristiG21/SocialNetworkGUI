package ubb.scs.map.service;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.utils.events.ChangeEventType;
import ubb.scs.map.utils.events.UtilizatorEntityChangeEvent;
import ubb.scs.map.utils.observer.Observable;
import ubb.scs.map.utils.observer.Observer;

import java.util.*;

public class UtilizatorService implements Observable<UtilizatorEntityChangeEvent> {
    private static UtilizatorService instance = null;
    private final Repository<Long, Utilizator> repoUtilizator;
    private final List<Observer<UtilizatorEntityChangeEvent>> observers = new ArrayList<>();

    private UtilizatorService(Repository<Long, Utilizator> repoUtilizator) {
        this.repoUtilizator = repoUtilizator;
    }

    public static UtilizatorService getInstance(Repository<Long, Utilizator> repoUtilizator) {
        if (instance == null) {
            instance = new UtilizatorService(repoUtilizator);
        }
        return instance;
    }

    public void addUtilizator(String firstName, String lastName) {
        Utilizator utilizator = new Utilizator(firstName, lastName);
        utilizator.setId(repoUtilizator.giveNewId());
        if (repoUtilizator.save(utilizator).isPresent())
            throw new ServiceException("A aparut o eroare la adaugarea utilizatorului!");
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.ADD, null));
    }

    public void modifyUtilizator(Long id, String firstName, String lastName) {
        Utilizator utilizator = new Utilizator(firstName, lastName);
        utilizator.setId(id);

        Optional<Utilizator> result;
        try {
            result = repoUtilizator.update(utilizator);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("A aparut o eroare la modificarea utilizatorului!");
        }
        if (result.isPresent())
            throw new ServiceException("A aparut o eroare la modificarea utilizatorului!");
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, null));
    }

    public void removeUtilizator(Long id) {
        Optional<Utilizator> result;
        try {
            result = repoUtilizator.delete(id);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("A aparut o eroare la stergerea utilizatorului!");
        }
        if (result.isEmpty())
            throw new ServiceException("A aparut o eroare la stergerea utilizatorului!");
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, null));
    }

    public Iterable<Utilizator> getAllUtilizatori() {
        return repoUtilizator.findAll();
    }

    @Override
    public void addObserver(Observer<UtilizatorEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UtilizatorEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UtilizatorEntityChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
