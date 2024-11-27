package ubb.scs.map.repository.file;

import ubb.scs.map.domain.Entity;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final String filename;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename = fileName;
        readFromFile();
    }

    public abstract E createEntity(String line);

    public abstract String saveEntity(E entity);

    @Override
    public Optional<E> findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Collection<E> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if (e.isEmpty())
            writeToFile();
        return e;
    }

    private void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            entities.values().stream().map(this::saveEntity).forEach(entity -> writeEntityToFile(writer, entity));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeEntityToFile(BufferedWriter writer, String entity) {
        try {
            writer.write(entity);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                E entity = createEntity(line);
                entities.put(entity.getId(), entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> entity = super.delete(id);
        if (entity.isPresent())
            writeToFile();
        return entity;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> result = super.update(entity);
        if (result.isEmpty())
            writeToFile();
        return result;
    }
}
