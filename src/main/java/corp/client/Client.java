package corp.client;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Client {
    private long id;
    private String name;
    private LocalDate birthday;
    private Gender gender;

    public enum Gender {
        male,
        female
    }
}