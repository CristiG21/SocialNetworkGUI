package ubb.scs.map.repository.file;

import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.domain.validators.Validator;

import java.util.Collections;

public class UtilizatorRepository extends AbstractFileRepository<Long, Utilizator> {
    public UtilizatorRepository(Validator<Utilizator> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Utilizator createEntity(String line) {
        String[] splited = line.split(";");
        Utilizator u = new Utilizator(splited[1], splited[2]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    @Override
    public String saveEntity(Utilizator entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }

    @Override
    public Long giveNewId() {
        return (entities.isEmpty()) ? 0 : (Collections.max(entities.keySet()) + 1);
    }
}
