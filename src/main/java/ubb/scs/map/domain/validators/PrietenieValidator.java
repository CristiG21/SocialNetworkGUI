package ubb.scs.map.domain.validators;


import ubb.scs.map.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie> {
    /**
     * Validates an prietenie
     *
     * @param entity prietenie to be validated
     * @throws ValidationException if the prietenie is not valid
     */
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if (entity.getUtilizator1Id() == null || entity.getUtilizator2Id() == null)
            throw new ValidationException("Prietenia nu este valida!");
    }
}
