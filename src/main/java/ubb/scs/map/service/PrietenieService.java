package ubb.scs.map.service;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.PrietenieDto;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.utils.events.ChangeEventType;
import ubb.scs.map.utils.events.PrietenieEntityChangeEvent;
import ubb.scs.map.utils.observer.Observable;
import ubb.scs.map.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;

public class PrietenieService implements Observable<PrietenieEntityChangeEvent> {
    private static PrietenieService instance = null;
    private final Repository<Long, Utilizator> repoUtilizator;
    private final Repository<Long, Prietenie> repoPrietenie;
    private List<Observer<PrietenieEntityChangeEvent>> observers = new ArrayList<>();

    private PrietenieService(Repository<Long, Utilizator> repoUtilizator, Repository<Long, Prietenie> repoPrietenie) {
        this.repoUtilizator = repoUtilizator;
        this.repoPrietenie = repoPrietenie;
    }

    public static PrietenieService getInstance(Repository<Long, Utilizator> repoUtilizator, Repository<Long, Prietenie> repoPrietenie) {
        if (instance == null) {
            instance = new PrietenieService(repoUtilizator, repoPrietenie);
        }
        return instance;
    }

    public void addPrietenie(Long user1Id, Long user2Id) {
        if (repoUtilizator.findOne(user1Id).isEmpty() || repoUtilizator.findOne(user2Id).isEmpty()) {
            throw new ServiceException("A aparut o eroare la adaugarea prieteniei!");
        }
        if (user1Id.equals(user2Id)) {
            throw new ServiceException("Nu se poate adauga o prietenie intre acelasi utilizator!");
        }

        Prietenie prietenie = new Prietenie(user1Id, user2Id, LocalDateTime.now(), false);
        prietenie.setId(repoPrietenie.giveNewId());
        if (repoPrietenie.save(prietenie).isPresent())
            throw new ServiceException("Aceasta prietenie exista deja!");
        notifyObservers(new PrietenieEntityChangeEvent(ChangeEventType.ADD, null));
    }

    public void removePrietenie(Long id) {
        repoPrietenie.delete(id);
        notifyObservers(new PrietenieEntityChangeEvent(ChangeEventType.DELETE, null));
    }

    public Iterable<Prietenie> getAllPrietenii() {
        return repoPrietenie.findAll();
    }

    public Iterable<PrietenieDto> getAllReceivedPrietenii(Long id) {
        Set<PrietenieDto> prietenii = new HashSet<>();
        repoPrietenie.findAll().forEach(prietenie -> {
            if (Objects.equals(prietenie.getUtilizator2Id(), id)) {
                prietenii.add(createPrietenieDto(prietenie, true));
            }
        });
        return prietenii;
    }

    public Iterable<PrietenieDto> getAllSentPrietenii(Long id) {
        Set<PrietenieDto> prietenii = new HashSet<>();
        repoPrietenie.findAll().forEach(prietenie -> {
            if (Objects.equals(prietenie.getUtilizator1Id(), id)) {
                prietenii.add(createPrietenieDto(prietenie, false));
            }
        });
        return prietenii;
    }

    public void acceptPrietenie(Long userId, Long prietenieId) {
        Optional<Prietenie> optionalPrietenie = repoPrietenie.findOne(prietenieId);
        if (optionalPrietenie.isEmpty())
            throw new ServiceException("A aparut o eroare la acceptarea prieteniei!");

        Prietenie prietenie = optionalPrietenie.get();
        if (!Objects.equals(prietenie.getUtilizator2Id(), userId))
            throw new ServiceException("Aceasta prietenie a fost trimisa de dumneavoastra, trebuie acceptata de cealalta persoana!");
        if (prietenie.getAccepted())
            throw new ServiceException("Aceasta prietenie este deja acceptata!");

        prietenie.setAccepted(true);
        repoPrietenie.update(prietenie);
        notifyObservers(new PrietenieEntityChangeEvent(ChangeEventType.UPDATE, null));
    }

    public PrietenieDto createPrietenieDto(Prietenie prietenie, boolean received) {
        Utilizator utilizator;
        if (received)
            utilizator = repoUtilizator.findOne(prietenie.getUtilizator1Id()).get();
        else
            utilizator = repoUtilizator.findOne(prietenie.getUtilizator2Id()).get();
        return new PrietenieDto(prietenie.getId(), utilizator.getFirstName(), utilizator.getLastName(), prietenie.getFriendsFrom(), prietenie.getAccepted());
    }

    @Override
    public void addObserver(Observer<PrietenieEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<PrietenieEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(PrietenieEntityChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }
}
