package corp.prefs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Prefs {
    public static final String DEFAULT_PREFS_FILENAME = "./json/prefs.json";
    public static final String DB_JDBC_CONNECTION_URL = "dbUrl";
    public static final String DB_JDBC_CONNECTION_USER = "dbUser";
    public static final String DB_JDBC_CONNECTION_PASSWORD = "dbPass";

    private Map<String, Object> prefsList = new HashMap<>();

    public Prefs() {
        this(DEFAULT_PREFS_FILENAME);
    }
    public Prefs(String filename) {
        try {
            String json = String.join(
                    "\n",
                    Files.readAllLines(Paths.get(filename))
            );

            TypeToken<?> typeToken = TypeToken.getParameterized(
                    Map.class,
                    String.class,
                    Object.class
            );

            prefsList = new Gson().fromJson(json, typeToken.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        return prefsList.get(key).toString();
    }
}
