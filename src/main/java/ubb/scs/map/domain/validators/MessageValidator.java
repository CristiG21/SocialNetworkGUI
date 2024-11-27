package ubb.scs.map.domain.validators;


import ubb.scs.map.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        if (entity.getChatId() == null || entity.getUserId() == null || entity.getDate() == null || entity.getMessage().isEmpty())
            throw new ValidationException("Mesajul nu este valid!");
    }
}
