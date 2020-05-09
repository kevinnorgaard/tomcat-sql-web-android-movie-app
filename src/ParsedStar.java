import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParsedStar {
    private String name;

    private String dob;

    public ParsedStar() {
    }

    public ParsedStar(String name, String dob) {
        this.name = name;
        this.dob = dob;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star name: " + getName());
        sb.append(": ");
        sb.append("DOB:" + getDob());

        return sb.toString();
    }
}