import java.util.ArrayList;
import java.util.List;

public class Employee {
    private final String email;
    private final String fullName;

    public Employee(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}
