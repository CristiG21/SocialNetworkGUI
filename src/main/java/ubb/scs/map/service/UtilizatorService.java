package ubb.scs.map.service;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.PrietenieDto;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.utils.events.ChangeEventType;
import ubb.scs.map.utils.events.UtilizatorEntityChangeEvent;
import ubb.scs.map.utils.observer.Observable;
import ubb.scs.map.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;

public class UtilizatorService implements Observable<UtilizatorEntityChangeEvent> {
    private static UtilizatorService instance = null;
    private final Repository<Long, Utilizator> repoUtilizator;
    private final Repository<Long, Prietenie> repoPrietenie;
    private List<Observer<UtilizatorEntityChangeEvent>> observers = new ArrayList<>();

    private UtilizatorService(Repository<Long, Utilizator> repoUtilizator, Repository<Long, Prietenie> repoPrietenie) {
        this.repoUtilizator = repoUtilizator;
        this.repoPrietenie = repoPrietenie;
    }

    public static UtilizatorService getInstance(Repository<Long, Utilizator> repoUtilizator, Repository<Long, Prietenie> repoPrietenie) {
        if (instance == null) {
            instance = new UtilizatorService(repoUtilizator, repoPrietenie);
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

    public Optional<Utilizator> getUtilizator(Long id) {
        return repoUtilizator.findOne(id);
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
        observers.stream().forEach(x -> x.update(t));
    }

//    public int numberOfCommunities() {
//        Iterable<Utilizator> utilizatori = repoUtilizator.findAll();
//        Set<Long> vizUtilizatori = new HashSet<>();
//        int count = 0;
//
//        for (Utilizator u : utilizatori)
//            if (!vizUtilizatori.contains(u.getId())) {
//                ++count;
//                dfs(u.getId(), vizUtilizatori);
//            }
//
//        return count;
//    }
//
//    private List<Long> dfs(Long utilizatorId, Set<Long> vizUtilizatori) {
//        List<Long> component = new ArrayList<>();
//        vizUtilizatori.add(utilizatorId);
//        component.add(utilizatorId);
//
//        repoPrietenie.findAll().forEach(p -> {
//            if (p.getUtilizator1Id().equals(utilizatorId) && !vizUtilizatori.contains(p.getUtilizator2Id())) {
//                component.addAll(dfs(p.getUtilizator2Id(), vizUtilizatori));
//            } else if (p.getUtilizator2Id().equals(utilizatorId) && !vizUtilizatori.contains(p.getUtilizator1Id())) {
//                component.addAll(dfs(p.getUtilizator1Id(), vizUtilizatori));
//            }
//        });
//
//        return component;
//    }
//
//    public Iterable<Utilizator> mostSociableCommunity() {
//        Iterable<Long> sociableCommunity = new ArrayList<>();
//        Iterable<Utilizator> utilizatori = repoUtilizator.findAll();
//        Set<Long> vizUtilizatori = new HashSet<>();
//
//        int max = -1;
//        for (Utilizator u : utilizatori) {
//            if (!vizUtilizatori.contains(u.getId())) {
//                List<Long> aux = dfs(u.getId(), vizUtilizatori);
//                int l = longestPath(aux);
//                if (l > max) {
//                    sociableCommunity = aux;
//                    max = l;
//                }
//            }
//        }
//
//        List<Utilizator> sociableCommunityUtilizatori = new ArrayList<>();
//        for (Long u : sociableCommunity) {
//            Optional<Utilizator> utilizator = repoUtilizator.findOne(u);
//            utilizator.ifPresent(sociableCommunityUtilizatori::add);
//        }
//
//        return sociableCommunityUtilizatori;
//    }
//
//    private int longestPath(List<Long> community) {
//        int maxLength = 0;
//        for (Long u : community) {
//            Set<Long> visited = new HashSet<>();
//            maxLength = Math.max(maxLength, dfsLongestPath(u, visited));
//        }
//        return maxLength;
//    }
//
//    private int dfsLongestPath(Long utilizatorId, Set<Long> visited) {
//        visited.add(utilizatorId);
//        int maxLength = 0;
//
//        for (Prietenie p : repoPrietenie.findAll()) {
//            Long neighborId = null;
//            if (p.getUtilizator1Id().equals(utilizatorId) && !visited.contains(p.getUtilizator2Id())) {
//                neighborId = p.getUtilizator2Id();
//            } else if (p.getUtilizator2Id().equals(utilizatorId) && !visited.contains(p.getUtilizator1Id())) {
//                neighborId = p.getUtilizator1Id();
//            }
//
//            if (neighborId != null) {
//                maxLength = Math.max(maxLength, 1 + dfsLongestPath(neighborId, visited));
//            }
//        }
//
//        visited.remove(utilizatorId);
//        return maxLength;
//    }
}
