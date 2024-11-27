package ubb.scs.map.domain.validators;


import ubb.scs.map.domain.Chat;

public class ChatValidator implements Validator<Chat> {
    @Override
    public void validate(Chat entity) throws ValidationException {
        if (entity.getName().isEmpty() || entity.getAdminId() == null)
            throw new ValidationException("Chat-ul nu este valid!");
    }
}
