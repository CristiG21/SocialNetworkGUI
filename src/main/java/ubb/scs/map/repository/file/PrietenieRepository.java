package ubb.scs.map.repository.file;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.utils.Constants;

import java.time.LocalDateTime;
import java.util.Collections;

public class PrietenieRepository extends AbstractFileRepository<Long, Prietenie> {
    public PrietenieRepository(Validator<Prietenie> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Prietenie createEntity(String line) {
        String[] splitted = line.split(";");
        Prietenie u = new Prietenie(Long.parseLong(splitted[1]), Long.parseLong(splitted[2]), LocalDateTime.parse(splitted[3], Constants.DATE_TIME_FORMATTER), Boolean.valueOf(splitted[4]));
        u.setId(Long.parseLong(splitted[0]));
        return u;
    }

    @Override
    public String saveEntity(Prietenie entity) {
        return entity.getId() + ";" + entity.getUtilizator1Id() + ";" + entity.getUtilizator2Id() + ";" + entity.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER);
    }

    @Override
    public Long giveNewId() {
        return (entities.isEmpty()) ? 0 : (Collections.max(entities.keySet()) + 1);
    }
}
