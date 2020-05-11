public class ParsedStar {
    private String id;
    private String name;
    private int birthYear;

    public ParsedStar() {
        this.birthYear = -1;
    }

    public ParsedStar(String id, String name, int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("StarName: " + getName());
        sb.append("; ");
        sb.append("BirthYear: " + getBirthYear());

        return sb.toString();
    }
}