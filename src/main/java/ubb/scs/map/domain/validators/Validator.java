package ubb.scs.map.domain.validators;

public interface Validator<T> {
    /**
     * Validates an entity
     * @param entity entity to be validated
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}