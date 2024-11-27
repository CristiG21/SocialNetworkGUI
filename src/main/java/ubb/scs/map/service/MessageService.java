package ubb.scs.map.service;

import ubb.scs.map.domain.Message;
import ubb.scs.map.domain.MessageDto;
import ubb.scs.map.domain.Utilizator;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.utils.events.ChangeEventType;
import ubb.scs.map.utils.events.ChatEntityChangeEvent;
import ubb.scs.map.utils.events.MessageEntityChangeEvent;
import ubb.scs.map.utils.observer.Observable;
import ubb.scs.map.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;

public class MessageService implements Observable<MessageEntityChangeEvent> {
    private static MessageService instance = null;
    private final Repository<Long, Utilizator> repoUtilizator;
    private final Repository<Long, Message> repoMessage;
    private final List<Observer<MessageEntityChangeEvent>> observers = new ArrayList<>();

    private MessageService(Repository<Long, Utilizator> repoUtilizator, Repository<Long, Message> repoMessage) {
        this.repoUtilizator = repoUtilizator;
        this.repoMessage = repoMessage;
    }

    public static MessageService getInstance(Repository<Long, Utilizator> repoUtilizator, Repository<Long, Message> repoMessage) {
        if (instance == null) {
            instance = new MessageService(repoUtilizator, repoMessage);
        }
        return instance;
    }

    public void addMessage(Long userId, Long chatId, String messageText, Long replyMessageId) {
        Message message = new Message(userId, chatId, messageText, LocalDateTime.now(), replyMessageId);
        message.setId(repoMessage.giveNewId());
        repoMessage.save(message);
        notifyObservers(new MessageEntityChangeEvent(ChangeEventType.ADD, null));
    }

    private Message findMessage(Long messageId) {
        Optional<Message> message = repoMessage.findOne(messageId);
        return message.orElse(null);
    }

    public Iterable<MessageDto> getAllMessagesByChatId(Long id) {
        return repoMessage.findAll().stream()
                .filter(message -> Objects.equals(message.getChatId(), id))
                .sorted((message1, message2) -> message1.getDate().compareTo(message2.getDate()))
                .map(this::createMessageDto)
                .toList();
    }

    private MessageDto createMessageDto(Message message) {
        Utilizator utilizator = repoUtilizator.findOne(message.getUserId()).get();
        String userName = utilizator.getFirstName() + " " + utilizator.getLastName();
        String replyMessageText = null;
        if (message.getReplyMessageId() != null) {
            Message replyMessage = findMessage(message.getReplyMessageId());
            if (replyMessage != null)
                replyMessageText = replyMessage.getMessage();
        }
        MessageDto messageDto = new MessageDto(userName, message.getMessage(), replyMessageText);
        messageDto.setId(message.getId());
        return messageDto;
    }

    @Override
    public void addObserver(Observer<MessageEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageEntityChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
