package corp.storage;

import corp.prefs.Prefs;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class Storage {
    private static final Storage INSTANCE = new Storage();

    private final Connection connection;

    private Storage() {
        try {
            Prefs prefs = new Prefs();
            String dbUrl = prefs.getString(Prefs.DB_JDBC_CONNECTION_URL);
            String dbUser = prefs.getString(Prefs.DB_JDBC_CONNECTION_USER);
            String dbPass = prefs.getString(Prefs.DB_JDBC_CONNECTION_PASSWORD);

            new DatabaseInitService().initDb(dbUrl, dbUser, dbPass);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Storage getInstance() {
        return INSTANCE;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
