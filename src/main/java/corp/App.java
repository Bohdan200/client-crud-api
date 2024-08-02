package corp;

import corp.storage.Storage;
import corp.client.Client;
import corp.client.ClientDaoService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class App {
    public static void main(String[] args) throws SQLException {

        Connection connection = Storage.getInstance().getConnection();
        ClientDaoService daoService = new ClientDaoService(connection);

        Client newClient = new Client();
        newClient.setName("Bill Geitz");
        newClient.setBirthday(LocalDate.now());
        newClient.setGender(Client.Gender.male);
        long id = daoService.create(newClient);

        Client createdClient = daoService.getById(id);
        System.out.println("New client created in database: " + createdClient);

        Client updated = new Client();
        updated.setId(id);
        updated.setName("Liza Grace");
        updated.setBirthday(LocalDate.now().minusYears(30));
        updated.setGender(Client.Gender.female);
        daoService.update(updated);

        Client updatedClient = daoService.getById(id);
        System.out.println("The client has been updated in the database: " + updatedClient);

        Client second = new Client();
        second.setName("Bill Geitz");
        second.setBirthday(LocalDate.now().minusYears(36));
        second.setGender(Client.Gender.male);
        long secondId = daoService.create(second);

        Client secondClient = daoService.getById(secondId);
        System.out.println("Second client has been created: " + secondClient);

        List<Client> allClients = daoService.getAll();
        System.out.println("All clients in database:");
        for (Client client : allClients) {
            System.out.println(client);
        }

        boolean isSecondClient = daoService.exists(secondId);
        System.out.println("There is a client with this id " + secondId + " in the database: " + isSecondClient);

        String searchWord = "Gra";
        List<Client> findClients = daoService.searchByName(searchWord);
        System.out.println("Searching for the specified phrase by name - " + searchWord + ", returned the following clients from the database:");
        for (Client client : findClients) {
            System.out.println(client);
        }

        secondClient.setBirthday(LocalDate.now().minusYears(37));
        daoService.save(secondClient);
        Client savedClient = daoService.getById(secondId);
        System.out.println("Changes to client data have been saved for the following id = " + secondId + ": " + savedClient);

        daoService.deleteById(secondId);
        System.out.println("Client with id " + secondId + " has been removed from the database");

        List<Client> allClientsAfterDelete = daoService.getAll();
        System.out.println("All clients in database after deletion:");
        for (Client client : allClientsAfterDelete) {
            System.out.println(client);
        }

        daoService.clear();
        List<Client> allClientsAfterClear = daoService.getAll();
        System.out.println("List of clients from the database after cleanup:");
        for (Client client : allClientsAfterClear) {
            System.out.println(client);
        }
    }
}