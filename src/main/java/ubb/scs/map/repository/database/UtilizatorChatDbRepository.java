package ubb.scs.map.repository.database;

import ubb.scs.map.domain.UtilizatorChat;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.Repository;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UtilizatorChatDbRepository implements Repository<Long, UtilizatorChat> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<UtilizatorChat> validator;

    public UtilizatorChatDbRepository(String url, String username, String password, Validator<UtilizatorChat> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<UtilizatorChat> findOne(Long id) {
        return Optional.empty();
    }

    @Override
    public Collection<UtilizatorChat> findAll() {
        Set<UtilizatorChat> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users_chat");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long userId = resultSet.getLong("user_id");
                Long chatId = resultSet.getLong("chat_id");

                UtilizatorChat utilizatorChat = new UtilizatorChat(userId, chatId);
                messages.add(utilizatorChat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Optional<UtilizatorChat> save(UtilizatorChat entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users_chat (user_id, chat_id) VALUES (?, ?)")
        ) {
            statement.setLong(1, entity.getUserId());
            statement.setLong(2, entity.getChatId());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Optional<UtilizatorChat> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<UtilizatorChat> update(UtilizatorChat entity) {
        return Optional.empty();
    }

    @Override
    public Long giveNewId() {
        return null;
    }
}
