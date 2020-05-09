import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParsedMovie {
    private String id;

    private String title;

    private String director;

    private int year;

    private Set<String> genres;

    private Set<String> stars;

    public ParsedMovie() {
        this.genres = new HashSet<>();
        this.stars = new HashSet<>();
    }

    public ParsedMovie(String id, String title, String director, int year, Set<String> genres, Set<String> stars) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
        this.genres = genres;
        this.stars = stars;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public void addGenre(String genre) {
        this.genres.add(genre);
    }

    public Set<String> getGenres() {
        return this.genres;
    }

    public void addStar(String star) {
        this.stars.add(star);
    }

    public Set<String> getStars() {
        return this.stars;
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
        sb.append("Title:" + getTitle());
        sb.append("; ");
        sb.append("Director:" + getDirector());
        sb.append("; ");
        sb.append("Year:" + getYear());
        sb.append("; ");
        sb.append("Genres:" + String.join(",", getGenres()));
        sb.append("; ");
        sb.append("Stars:" + String.join(",", getStars()));

        return sb.toString();
    }
}