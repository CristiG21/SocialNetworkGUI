package ubb.scs.map.service;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.PrietenieDto;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.utils.events.ChangeEventType;
import ubb.scs.map.utils.events.UtilizatorEntityChangeEvent;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class PrietenieService {
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
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.ADD, null));
    }

    public void removePrietenie(Long id) {
        repoPrietenie.delete(id);
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, null));
    }

    public Iterable<Prietenie> getAllPrietenii() {
        return repoPrietenie.findAll();
    }

    public Iterable<PrietenieDto> getAllPrieteniiByUtilizator(Long id) {
        Set<PrietenieDto> prietenii = new HashSet<>();
        repoPrietenie.findAll().forEach(prietenie -> {
            if (Objects.equals(prietenie.getUtilizator1Id(), id) || Objects.equals(prietenie.getUtilizator2Id(), id)) {
                prietenii.add(createPrietenieDto(prietenie));
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
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, null));
    }

    public PrietenieDto createPrietenieDto(Prietenie prietenie) {
        Utilizator utilizator1 = repoUtilizator.findOne(prietenie.getUtilizator1Id()).get();
        Utilizator utilizator2 = repoUtilizator.findOne(prietenie.getUtilizator2Id()).get();
        PrietenieDto prietenieDto = new PrietenieDto(utilizator1.getFirstName(), utilizator1.getLastName(), utilizator2.getFirstName(), utilizator2.getLastName(), prietenie.getFriendsFrom(), prietenie.getAccepted());
        prietenieDto.setId(prietenie.getId());
        return prietenieDto;
    }
}
