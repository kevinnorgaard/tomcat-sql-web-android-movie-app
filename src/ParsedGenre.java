public class ParsedGenre {
    private int id;
    private String name;

    public ParsedGenre() {
    }

    public ParsedGenre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ID:" + getId());
        sb.append("; ");
        sb.append("Name: " + getName());
        sb.append("; ");
        return sb.toString();
    }
}
