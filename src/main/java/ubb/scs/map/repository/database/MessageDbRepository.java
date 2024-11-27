package ubb.scs.map.repository.database;

import ubb.scs.map.domain.Message;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MessageDbRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Message message = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long userId = resultSet.getLong("user_id");
                Long chatId = resultSet.getLong("chat_id");
                String text = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long replyMessageId = resultSet.getLong("reply_message_id");

                message = new Message(userId, chatId, text, date, replyMessageId);
                message.setId(id);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(message);
    }

    @Override
    public Collection<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long userId = resultSet.getLong("user_id");
                Long chatId = resultSet.getLong("chat_id");
                String text = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long replyMessageId = resultSet.getLong("reply_message_id");

                Message message = new Message(userId, chatId, text, date, replyMessageId);
                message.setId(id);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (user_id, chat_id, message, date, reply_message_id) VALUES (?, ?, ?, ?, ?)")
        ) {
            statement.setLong(1, entity.getUserId());
            statement.setLong(2, entity.getChatId());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
            if (entity.getReplyMessageId() != null)
                statement.setLong(5, entity.getReplyMessageId());
            else
                statement.setNull(5, Types.BIGINT);
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public Long giveNewId() {
        return null;
    }
}
