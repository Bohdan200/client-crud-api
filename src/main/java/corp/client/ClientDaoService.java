package corp.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class ClientDaoService {
    private final PreparedStatement createSt;
    private final PreparedStatement getByIdSt;
    private final PreparedStatement selectMaxIdSt;
    private final PreparedStatement getAllSt;
    private final PreparedStatement updateSt;
    private final PreparedStatement deleteByIdSt;
    private final PreparedStatement existsByIdSt;
    private final PreparedStatement clearSt;
    private final PreparedStatement searchSt;

    public ClientDaoService(Connection connection) throws SQLException {
        createSt = connection.prepareStatement(
                "INSERT INTO client (name, birthday, gender) VALUES(?, ?, ?)"
        );

        getByIdSt = connection.prepareStatement(
                "SELECT name, birthday, gender FROM client WHERE id = ?"
        );

        getAllSt = connection.prepareStatement(
                "SELECT id, name, birthday, gender FROM client"
        );

        updateSt = connection.prepareStatement(
                "UPDATE client SET name = ?, birthday = ?, gender = ? WHERE id = ?"
        );

        selectMaxIdSt = connection.prepareStatement(
                "SELECT max(id) AS maxId FROM client"
        );

        deleteByIdSt = connection.prepareStatement(
                "DELETE FROM client WHERE id = ?"
        );

        existsByIdSt = connection.prepareStatement(
                "SELECT count(*) > 0 AS clientExists FROM client WHERE id = ?"
        );

        clearSt = connection.prepareStatement(
                "DELETE FROM client"
        );

        searchSt = connection.prepareStatement(
                "SELECT id, name, birthday, gender FROM client WHERE name LIKE ?"
        );
    }

    public long create(Client client) throws SQLException {
        createSt.setString(1, client.getName());
        createSt.setString(2,
                client.getBirthday() == null ? null : client.getBirthday().toString());
        createSt.setString(3,
                client.getGender() == null ? null : client.getGender().name());
        createSt.executeUpdate();

        long id;

        try(ResultSet rs = selectMaxIdSt.executeQuery()) {
            rs.next();
            id = rs.getLong("maxId");
        }

        return id;
    }

    public Client getById(long id) throws SQLException {
        getByIdSt.setLong(1, id);

        try(ResultSet rs = getByIdSt.executeQuery()) {
            if (!rs.next()) {
                return null;
            }

            Client result = new Client();
            result.setId(id);
            result.setName(rs.getString("name"));

            String birthday = rs.getString("birthday");
            if (birthday != null) {
                result.setBirthday(LocalDate.parse(birthday));
            }

            String gender = rs.getString("gender");
            if (gender != null) {
                result.setGender(Client.Gender.valueOf(gender));
            }

            return result;
        }
    }

    public List<Client> getAll() throws SQLException {
        return getClients(getAllSt);
    }

    public void update(Client client) throws SQLException {
        updateSt.setString(1, client.getName());
        updateSt.setString(2, client.getBirthday().toString());
        updateSt.setString(3, client.getGender().name());
        updateSt.setLong(4, client.getId());

        updateSt.executeUpdate();
    }

    public void deleteById(long id) throws SQLException {
        deleteByIdSt.setLong(1, id);
        deleteByIdSt.executeUpdate();
    }

    public List<Client> searchByName(String query) throws SQLException {
        searchSt.setString(1, "%" + query + "%");

        return getClients(searchSt);
    }

    public boolean exists(long id) throws SQLException {
        existsByIdSt.setLong(1, id);
        try(ResultSet rs = existsByIdSt.executeQuery()) {
            rs.next();

            return rs.getBoolean("clientExists");
        }
    }

    public long save(Client client) throws SQLException {
        if (exists(client.getId())) {
            update(client);
            return client.getId();
        }

        return create(client);
    }

    public void clear() throws SQLException {
        clearSt.executeUpdate();
    }

    private List<Client> getClients(PreparedStatement st) throws SQLException {
        try(ResultSet rs = st.executeQuery()) {
            List<Client> result = new ArrayList<>();

            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getLong("id"));
                client.setName(rs.getString("name"));

                String birthday = rs.getString("birthday");
                if (birthday != null) {
                    client.setBirthday(LocalDate.parse(birthday));
                }

                String gender = rs.getString("gender");
                if (gender != null) {
                    client.setGender(Client.Gender.valueOf(gender));
                }

                result.add(client);
            }

            return result;
        }
    }
}
