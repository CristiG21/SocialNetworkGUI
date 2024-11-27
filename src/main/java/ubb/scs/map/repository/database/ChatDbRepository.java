package ubb.scs.map.repository.database;

import ubb.scs.map.domain.Chat;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.Repository;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ChatDbRepository implements Repository<Long, Chat> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Chat> validator;

    public ChatDbRepository(String url, String username, String password, Validator<Chat> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Chat> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Chat chat = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from chats WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Long adminId = resultSet.getLong("admin_id");

                chat = new Chat(name, adminId);
                chat.setId(id);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(chat);
    }

    @Override
    public Collection<Chat> findAll() {
        Set<Chat> chats = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from chats");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Long adminId = resultSet.getLong("admin_id");

                Chat chat = new Chat(name, adminId);
                chat.setId(id);
                chats.add(chat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }

    @Override
    public Optional<Chat> save(Chat entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO chats (name, admin_id) VALUES (?, ?)")
        ) {
            statement.setString(1, entity.getName());
            statement.setLong(2, entity.getAdminId());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Optional<Chat> delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Optional<Chat> chat = findOne(id);
        if (chat.isEmpty())
            return Optional.empty();
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM chats WHERE id = ?")
        ) {
            statement.setLong(1, id);
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return chat;
        return Optional.empty();
    }

    @Override
    public Optional<Chat> update(Chat entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity nu poate fi null!");
        validator.validate(entity);

        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE chats SET name = ?, admin_id = ? WHERE id = ?")
        ) {
            statement.setString(1, entity.getName());
            statement.setLong(2, entity.getAdminId());
            statement.setLong(3, entity.getId());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Long giveNewId() {
        return null;
    }
}
