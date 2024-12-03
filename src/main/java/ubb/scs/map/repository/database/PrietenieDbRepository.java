package ubb.scs.map.repository.database;

import ubb.scs.map.domain.Prietenie;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.dto.PrietenieFilterDto;
import ubb.scs.map.utils.Pair;
import ubb.scs.map.utils.paging.Page;
import ubb.scs.map.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PrietenieDbRepository implements PrietenieRepository {
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

    private Pair<String, List<Object>> toSql(PrietenieFilterDto filter) {
        if (filter == null) {
            return new Pair<>("", Collections.emptyList());
        }

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (filter.getUserId().isPresent()) {
            if (filter.getReceived().isEmpty()) {
                conditions.add("user1_id = ? OR user2_id = ?");
                params.add(filter.getUserId().get());
                params.add(filter.getUserId().get());
            } else if (filter.getReceived().get()) {
                conditions.add("user2_id = ?");
                params.add(filter.getUserId().get());
            } else {
                conditions.add("user1_id = ?");
                params.add(filter.getUserId().get());
            }
        }

        String sql = String.join(" AND ", conditions);
        return new Pair<>(sql, params);
    }

    private int count(Connection connection, PrietenieFilterDto filter) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM friendships";
        Pair<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getFirst().isEmpty()) {
            sql += " WHERE " + sqlFilter.getFirst();
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param);
            }
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfFriendships = 0;
                if (result.next()) {
                    totalNumberOfFriendships = result.getInt("count");
                }
                return totalNumberOfFriendships;
            }
        }
    }

    private List<Prietenie> findAllOnPage(Connection connection, Pageable pageable, PrietenieFilterDto filter) throws SQLException {
        List<Prietenie> friendshipsOnPage = new ArrayList<>();

        String sql = "SELECT * FROM friendships";
        Pair<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getFirst().isEmpty()) {
            sql += " WHERE " + sqlFilter.getFirst();
        }
        sql += " LIMIT ? OFFSET ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param);
            }
            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    Long user1Id = resultSet.getLong("user1_id");
                    Long user2Id = resultSet.getLong("user2_id");
                    LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();
                    Boolean accepted = resultSet.getBoolean("accepted");

                    Prietenie prietenie = new Prietenie(user1Id, user2Id, friendsFrom, accepted);
                    prietenie.setId(id);
                    friendshipsOnPage.add(prietenie);
                }
            }
        }
        return friendshipsOnPage;
    }

    @Override
    public Page<Prietenie> findAllOnPage(Pageable pageable, PrietenieFilterDto filter) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int totalNumberOfFriendships = count(connection, filter);
            List<Prietenie> friendshipsOnPage;
            if (totalNumberOfFriendships > 0) {
                friendshipsOnPage = findAllOnPage(connection, pageable, filter);
            } else {
                friendshipsOnPage = new ArrayList<>();
            }
            return new Page<>(friendshipsOnPage, totalNumberOfFriendships);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Prietenie> findAllOnPage(Pageable pageable) {
        return findAllOnPage(pageable, null);
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
