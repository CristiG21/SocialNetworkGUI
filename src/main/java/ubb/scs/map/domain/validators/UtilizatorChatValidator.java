package ubb.scs.map.domain.validators;


import ubb.scs.map.domain.UtilizatorChat;

public class UtilizatorChatValidator implements Validator<UtilizatorChat> {
    @Override
    public void validate(UtilizatorChat entity) throws ValidationException {
        if (entity.getChatId() == null || entity.getUserId() == null)
            throw new ValidationException("UtilizatoriChat nu este valid!");
    }
}
