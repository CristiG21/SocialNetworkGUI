package ubb.scs.map.service;

import ubb.scs.map.domain.*;
import ubb.scs.map.exceptions.ServiceException;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.utils.events.ChangeEventType;
import ubb.scs.map.utils.events.ChatEntityChangeEvent;
import ubb.scs.map.utils.observer.Observable;
import ubb.scs.map.utils.observer.Observer;

import java.util.*;

public class ChatService implements Observable<ChatEntityChangeEvent> {
    private static ChatService instance = null;
    private final Repository<Long, Chat> repoChat;
    private final Repository<Long, UtilizatorChat> repoUtilizatorChat;
    private final List<Observer<ChatEntityChangeEvent>> observers = new ArrayList<>();

    private ChatService(Repository<Long, Chat> repoChat, Repository<Long, UtilizatorChat> repoUtilizatorChat) {
        this.repoChat = repoChat;
        this.repoUtilizatorChat = repoUtilizatorChat;
    }

    public static ChatService getInstance(Repository<Long, Chat> repoChat, Repository<Long, UtilizatorChat> repoUtilizatorChat) {
        if (instance == null) {
            instance = new ChatService(repoChat, repoUtilizatorChat);
        }
        return instance;
    }

    public void addChat(String name, Long adminId) {
        Chat chat = new Chat(name, adminId);
        chat.setId(repoChat.giveNewId());
        if (repoChat.save(chat).isPresent())
            throw new ServiceException("Acest chat exista deja!");
        notifyObservers(new ChatEntityChangeEvent(ChangeEventType.ADD, null));
    }

    public void modifyChat(Long id, String name, Long admin_id) {
        Chat chat = new Chat(name, admin_id);
        chat.setId(id);

        Optional<Chat> result;
        try {
            result = repoChat.update(chat);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("A aparut o eroare la modificarea chat-ului!");
        }
        if (result.isPresent())
            throw new ServiceException("A aparut o eroare la modificarea chat-ului!");
        notifyObservers(new ChatEntityChangeEvent(ChangeEventType.UPDATE, null));
    }

    public void removeChat(Long id, Long userId) {
        if (!Objects.equals(userId, findChat(id).getAdminId()))
            throw new ServiceException("Nu sunteti administrator al acestui chat!");
        repoChat.delete(id);
        notifyObservers(new ChatEntityChangeEvent(ChangeEventType.DELETE, null));
    }

    private Chat findChat(Long chatId) {
        Optional<Chat> chat = repoChat.findOne(chatId);
        return chat.orElse(null);
    }

    public Iterable<Chat> getAllChatsByUserId(Long id) {
        Set<Chat> chats = new HashSet<>();
        repoChat.findAll().stream()
                .filter(chat -> Objects.equals(chat.getAdminId(), id))
                .forEach(chats::add);
        repoUtilizatorChat.findAll().stream()
                .filter(utilizatorChat -> Objects.equals(utilizatorChat.getUserId(), id))
                .map(utilizatorChat -> findChat(utilizatorChat.getChatId()))
                .forEach(chats::add);
        return chats;
    }

    public void addUtilizatoriChat(List<Utilizator> utilizatori, Long chatId) {
        Chat chat = findChat(chatId);
        utilizatori.stream()
                .filter(utilizator -> !Objects.equals(utilizator.getId(), chat.getAdminId()))
                .forEach(utilizator -> {
                    UtilizatorChat utilizatorChat = new UtilizatorChat(utilizator.getId(), chatId);
                    repoUtilizatorChat.save(utilizatorChat);
                });
    }

    @Override
    public void addObserver(Observer<ChatEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<ChatEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(ChatEntityChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
