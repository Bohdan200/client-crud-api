package corp;

import corp.prefs.Prefs;
import corp.storage.DatabaseInitService;
import corp.client.Client;
import corp.client.ClientDaoService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

class ClientDaoServiceTests {
    private Connection connection;
    private ClientDaoService daoService;

    @BeforeEach
    public void beforeEach() throws SQLException {
        Prefs prefs = new Prefs();
        final String connectionUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        final String dbUser = prefs.getString(Prefs.DB_JDBC_CONNECTION_USER);
        final String dbPass = prefs.getString(Prefs.DB_JDBC_CONNECTION_PASSWORD);

        new DatabaseInitService().initDb(connectionUrl, dbUser, dbPass);
        connection = DriverManager.getConnection(connectionUrl, dbUser, dbPass);

        daoService = new ClientDaoService(connection);

        daoService.clear();
    }

    @AfterEach
    public void afterEach() throws SQLException {
        connection.close();
    }

    @Test
    public void testThatClientCreatedCorrectly() throws SQLException {
        List<Client> originalClients = new ArrayList<>();

        Client fullValueClient = new Client();
        fullValueClient.setName("TestName 1");
        fullValueClient.setBirthday(LocalDate.now());
        fullValueClient.setGender(Client.Gender.male);
        originalClients.add(fullValueClient);

        Client nullBirthdayClient = new Client();
        nullBirthdayClient.setName("TestName 2");
        nullBirthdayClient.setBirthday(null);
        nullBirthdayClient.setGender(Client.Gender.male);
        originalClients.add(nullBirthdayClient);

        Client nullGenderClient = new Client();
        nullGenderClient.setName("TestName 3");
        nullGenderClient.setBirthday(LocalDate.now());
        nullGenderClient.setGender(null);
        originalClients.add(nullGenderClient);

        for (Client original : originalClients) {
            long id = daoService.create(original);
            Client saved = daoService.getById(id);

            Assertions.assertEquals(id, saved.getId());
            Assertions.assertEquals(original.getName(), saved.getName());
            Assertions.assertEquals(original.getBirthday(), saved.getBirthday());
            Assertions.assertEquals(original.getGender(), saved.getGender());
        }
    }

    @Test
    public void getAllTest() throws SQLException {
        Client expected = new Client();
        expected.setName("TestName 1");
        expected.setBirthday(LocalDate.now());
        expected.setGender(Client.Gender.male);

        long id = daoService.create(expected);
        expected.setId(id);

        List<Client> expectedClients = Collections.singletonList(expected);
        List<Client> actualClients = daoService.getAll();

        Assertions.assertEquals(expectedClients, actualClients);
    }

    @Test
    public void testUpdateClientWorkCorrectly() throws SQLException {
        List<Client> originalClients = new ArrayList<>();

        Client fullValueClient = new Client();
        fullValueClient.setName("TestName 1");
        fullValueClient.setBirthday(LocalDate.now());
        fullValueClient.setGender(Client.Gender.male);
        originalClients.add(fullValueClient);

        Client nullBirthdayClient = new Client();
        nullBirthdayClient.setName("TestName 2");
        nullBirthdayClient.setBirthday(null);
        nullBirthdayClient.setGender(Client.Gender.male);
        originalClients.add(nullBirthdayClient);

        Client nullGenderClient = new Client();
        nullGenderClient.setName("TestName 3");
        nullGenderClient.setBirthday(LocalDate.now());
        nullGenderClient.setGender(null);
        originalClients.add(nullGenderClient);

        byte n = 1;
        for (Client original : originalClients) {
            long id = daoService.create(original);
            original.setId(id);

            original.setName("NewName " + n);
            original.setBirthday(LocalDate.now().plusDays(1));
            original.setGender(Client.Gender.female);

            daoService.update(original);
            Client updated = daoService.getById(id);

            Assertions.assertEquals(id, updated.getId());
            Assertions.assertEquals("NewName " + n, updated.getName());
            Assertions.assertEquals(LocalDate.now().plusDays(1), updated.getBirthday());
            Assertions.assertEquals(Client.Gender.female, updated.getGender());

            n += 1;
        }
    }

    @Test
    public void testDelete() throws SQLException {
        Client expected = new Client();
        expected.setName("TestName");
        expected.setBirthday(LocalDate.now());
        expected.setGender(Client.Gender.male);

        long id = daoService.create(expected);
        daoService.deleteById(id);

        Assertions.assertNull(daoService.getById(id));
    }

    @Test
    public void testExists() throws SQLException {
        Client expected = new Client();
        expected.setName("TestName");
        expected.setBirthday(LocalDate.now());
        expected.setGender(Client.Gender.male);

        long id = daoService.create(expected);
        Assertions.assertTrue(daoService.exists(id));
    }

    @Test
    public void testThatExistsReturnsFalseForNonExistingClient() throws SQLException {
        Assertions.assertFalse(daoService.exists(-1));
    }

    @Test
    public void testSaveOnNewClient() throws SQLException {
        Client newClient = new Client();
        newClient.setName("TestName");
        newClient.setBirthday(LocalDate.now());
        newClient.setGender(Client.Gender.male);

        long id = daoService.save(newClient);
        Assertions.assertTrue(daoService.exists(id));
    }

    @Test
    public void testSaveOnExistingNewClient() throws SQLException {
        Client newClient = new Client();
        newClient.setName("TestName");
        newClient.setBirthday(LocalDate.now());
        newClient.setGender(Client.Gender.male);

        long id = daoService.save(newClient);
        newClient.setId(id);

        newClient.setName("New Name");
        daoService.save(newClient);

        Client updated = daoService.getById(id);
        Assertions.assertEquals("New Name", updated.getName());
    }

    @Test
    public void testSearchOnEmpty() throws SQLException {
        Assertions.assertEquals(
                Collections.emptyList(),
                daoService.searchByName("name")
        );
    }

    @Test
    public void testSearchOnFilledDb() throws SQLException {
        Client newClient = new Client();
        newClient.setName("TestName");
        newClient.setBirthday(LocalDate.now());
        newClient.setGender(Client.Gender.male);

        long id = daoService.save(newClient);
        List<Client> actual = daoService.searchByName("Test");

        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(id, actual.get(0).getId());
    }
}