package ubb.scs.map.domain.validators;


import ubb.scs.map.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    /**
     * Validates an utilizator
     *
     * @param entity utilizator to be validated
     * @throws ValidationException if the utilizator is not valid
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if (entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            throw new ValidationException("Utilizatorul nu este valid!");
    }
}
