import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParsedMovie {
    private String id;
    private String title;
    private String director;
    private int year;

    public ParsedMovie() {
    }

    public ParsedMovie(String id, String title, String director, int year) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTDY() {
        return getTitle() + ";" + getDirector() + ";" + getYear();
    }

    public String getTD() {
        return getTitle() + ";" + getDirector();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ID:" + getId());
        sb.append("; ");
        sb.append(getTDY());
        return sb.toString();
    }
}