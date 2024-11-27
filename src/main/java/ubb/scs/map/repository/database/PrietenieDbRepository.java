package ubb.scs.map.repository.database;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrietenieDbRepository implements Repository<Long, Prietenie> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Prietenie> validator;

    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Prietenie> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Prietenie prietenie = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships WHERE id = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long user1Id = resultSet.getLong("user1_id");
                Long user2Id = resultSet.getLong("user2_id");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Boolean accepted = resultSet.getBoolean("accepted");

                prietenie = new Prietenie(user1Id, user2Id, friendsFrom, accepted);
                prietenie.setId(id);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(prietenie);
    }

    @Override
    public Collection<Prietenie> findAll() {
        Set<Prietenie> prietenii = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long user1Id = resultSet.getLong("user1_id");
                Long user2Id = resultSet.getLong("user2_id");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Boolean accepted = resultSet.getBoolean("accepted");

                Prietenie prietenie = new Prietenie(user1Id, user2Id, friendsFrom, accepted);
                prietenie.setId(id);
                prietenii.add(prietenie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenii;
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        validator.validate(entity);

        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (user1_id, user2_id, friends_from, accepted) VALUES (?, ?, ?, ?)")
        ) {
            statement.setLong(1, entity.getUtilizator1Id());
            statement.setLong(2, entity.getUtilizator2Id());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setBoolean(4, entity.getAccepted());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        return Optional.of(entity);
    }

    @Override
    public Optional<Prietenie> delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null!");

        Optional<Prietenie> prietenie = findOne(id);
        if (prietenie.isEmpty())
            return Optional.empty();
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id = ?")
        ) {
            statement.setLong(1, id);
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return prietenie;
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity nu poate fi null!");
        validator.validate(entity);

        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET user1_id = ?, user2_id = ?, friends_from = ?, accepted = ? WHERE id = ?")
        ) {
            statement.setLong(1, entity.getUtilizator1Id());
            statement.setLong(2, entity.getUtilizator2Id());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setBoolean(4, entity.getAccepted());
            statement.setLong(5, entity.getId());
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
