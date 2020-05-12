import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Pair {
    String first;
    String second;
    public Pair() {}
    public Pair(String first, String second) {
        this.first = first;
        this.second = second;
    }
    public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getSecond() {
        return second;
    }
    public void setSecond(String second) {
        this.second = second;
    }
    public String toString() {
        return first + ";" + second;
    }
}

public class MyDOMParser {
    Map<String, ParsedMovie> existingMoviesByID;
    Map<String, ParsedMovie> existingMoviesByTDY;
    Map<String, ParsedMovie> existingMoviesByTD;
    Map<String, ParsedStar> existingStarsByID;
    Map<String, ParsedStar> existingStarsByName;
    Map<String, ParsedGenre> existingGenresByID;
    Map<String, ParsedGenre> existingGenresByName;
    Set<Pair> existingStarsInMovies;
    Set<Pair> existingGenresInMovies;

    Map<String, ParsedMovie> newMoviesByFID; // key = fid (to connect mains.xml & cast.xml)
    Map<String, ParsedMovie> newMoviesByTDY; // key = title, director, year (to check for duplicates)
    Map<String, ParsedMovie> newMoviesByTD; // key = title, director (to add mains.xml & cast.xml if FID fails)
    Map<String, ParsedStar> newStars;
    Set<String> newGenres;
    Set<Pair> newStarsInMovies;
    Set<Pair> newGenresInMovies;

    Document dom;
    List<String> errors;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public MyDOMParser() {
        existingMoviesByID = new HashMap<>();
        existingMoviesByTDY = new HashMap<>();
        existingMoviesByTD = new HashMap<>();
        existingStarsByID = new HashMap<>();
        existingStarsByName = new HashMap<>();
        existingGenresByID = new HashMap<>();
        existingGenresByName = new HashMap<>();
        existingStarsInMovies = new HashSet<>();
        existingGenresInMovies = new HashSet<>();

        newMoviesByFID = new HashMap<>();
        newMoviesByTDY = new HashMap<>();
        newMoviesByTD = new HashMap<>();
        newStars = new HashMap<>();
        newGenres = new HashSet<>();
        newStarsInMovies = new HashSet<>();
        newGenresInMovies = new HashSet<>();
        errors = new ArrayList<>();
    }

    public void loadExisting() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection connection = DriverManager.getConnection("jdbc:" + "mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                    "mytestuser", "mypassword");

            String query = "SELECT * FROM movies";
            PreparedStatement select = connection.prepareStatement(query);

            ResultSet result = select.executeQuery();

            while (result.next()) {
                String movieId = result.getString("id");
                String movieTitle = result.getString("title");
                String movieDirector = result.getString("director");
                int movieYear = result.getInt("year");
                ParsedMovie m = new ParsedMovie(movieId, movieTitle, movieDirector, movieYear);
                existingMoviesByID.put(m.getId(), m);
                existingMoviesByTDY.put(m.getTDY(), m);
                existingMoviesByTD.put(m.getTD(), m);
            }

            result.close();
            select.close();

            String query2 = "SELECT * FROM stars";
            PreparedStatement select2 = connection.prepareStatement(query2);

            ResultSet result2 = select2.executeQuery();

            while (result2.next()) {
                String starId = result2.getString("id");
                String starName = result2.getString("name");
                Object starBirthYearObj = result2.getObject("birthYear");
                int starBirthYear = starBirthYearObj != null ? (int)starBirthYearObj : -1;
                ParsedStar s = new ParsedStar(starId, starName, starBirthYear);
                existingStarsByName.put(starName, s);
                existingStarsByID.put(starId, s);
            }

            result2.close();
            select2.close();

            String query3 = "SELECT * FROM genres";
            PreparedStatement select3 = connection.prepareStatement(query3);

            ResultSet result3 = select3.executeQuery();

            while (result3.next()) {
                int genreId = result3.getInt("id");
                String genreName = result3.getString("name");
                ParsedGenre g = new ParsedGenre(genreId, genreName);
                existingGenresByName.put(genreName, g);
                existingGenresByID.put(genreId + "", g);
            }

            result3.close();
            select3.close();

            String query4 = "SELECT * FROM stars_in_movies";
            PreparedStatement select4 = connection.prepareStatement(query4);

            ResultSet result4 = select4.executeQuery();

            while (result4.next()) {
                String starId = result4.getString("starId");
                String movieId = result4.getString("movieId");
                ParsedStar s = existingStarsByID.get(starId);
                ParsedMovie m = existingMoviesByID.get(movieId);
                if (s != null && m != null) {
                    Pair starInMovie = new Pair(s.getName(), m.getTDY());
                    existingStarsInMovies.add(starInMovie);
                }
            }

            result4.close();
            select4.close();

            String query5 = "SELECT * FROM genres_in_movies";
            PreparedStatement select5 = connection.prepareStatement(query5);

            ResultSet result5 = select5.executeQuery();

            while (result5.next()) {
                String genreId = result5.getString("genreId");
                String movieId = result5.getString("movieId");
                ParsedGenre g = existingGenresByID.get(genreId);
                ParsedMovie m = existingMoviesByID.get(movieId);
                if (g != null && m != null) {
                    Pair genreInMovie = new Pair(g.getName(), m.getTDY());
                    existingGenresInMovies.add(genreInMovie);
                }
            }

            result5.close();
            result5.close();

            connection.close();
        } catch (Exception e) {
        }
    }

    public void addNew() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection connection = DriverManager.getConnection("jdbc:" + "mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                    "mytestuser", "mypassword");

            connection.setAutoCommit(false);

            String getMovieIdQuery = "SELECT id FROM movies_next_id";

            PreparedStatement getMovieIdStatement = connection.prepareStatement(getMovieIdQuery);
            ResultSet getMovieIdRs = getMovieIdStatement.executeQuery();

            connection.commit();

            if (getMovieIdRs.next()) {
                int nextMovieId = getMovieIdRs.getInt("id");

                String query1 = "insert into movies (id, title, year, director) values (?, ?, ?, ?) ";
                PreparedStatement statement1 = connection.prepareStatement(query1);

                for (ParsedMovie m : newMoviesByTDY.values()) {
                    String id = "tt" + nextMovieId;
                    m.setId(id);
                    existingMoviesByTDY.put(m.getTDY(), m);
                    statement1.setString(1, id);
                    statement1.setString(2, m.getTitle());
                    statement1.setInt(3, m.getYear());
                    statement1.setString(4, m.getDirector());
                    statement1.addBatch();
                    nextMovieId++;
                }

                statement1.executeBatch();
                connection.commit();

                String updateIdQuery = "UPDATE movies_next_id SET id = ?";
                PreparedStatement updateIdStatement = connection.prepareStatement(updateIdQuery);
                updateIdStatement.setInt(1, nextMovieId);
                updateIdStatement.executeUpdate();
                connection.commit();
            }

            String getStarIdQuery = "SELECT id FROM stars_next_id";

            PreparedStatement getStarIdStatement = connection.prepareStatement(getStarIdQuery);
            ResultSet getStarIdRs = getStarIdStatement.executeQuery();

            connection.commit();

            if (getStarIdRs.next()) {
                int nextStarId = getStarIdRs.getInt("id");

                String query2 = "insert into stars (id, name, birthYear) values (?, ?, ?) ";
                PreparedStatement statement2 = connection.prepareStatement(query2);

                List<ParsedStar> newStarsList = new ArrayList<ParsedStar>(newStars.values());
                for (int i = 0; i < newStarsList.size(); i++) {
                    ParsedStar s = newStarsList.get(i);
                    String id = "nm" + nextStarId;
                    s.setId(id);
                    existingStarsByName.put(s.getName(), s);
                    statement2.setString(1, id);
                    statement2.setString(2, s.getName());
                    statement2.setInt(3, s.getBirthYear());
                    statement2.addBatch();
                    nextStarId++;
                }

                statement2.executeBatch();
                connection.commit();

                String updateIdQuery = "UPDATE stars_next_id SET id = ?";
                PreparedStatement updateIdStatement = connection.prepareStatement(updateIdQuery);
                updateIdStatement.setInt(1, nextStarId);
                updateIdStatement.executeUpdate();
                connection.commit();
            }

            String starsInMoviesQuery = "insert into stars_in_movies (starId, movieId) values (?, ?)";
            PreparedStatement starsInMoviesStatement = connection.prepareStatement(starsInMoviesQuery);

            for (Pair sim : newStarsInMovies) {
                String starName = sim.getFirst();
                String movieTDY = sim.getSecond();
                ParsedStar s = existingStarsByName.get(starName);
                ParsedMovie m = existingMoviesByTDY.get(movieTDY);
                if (s != null && m != null) {
                    String starId = s.getId();
                    String movieId = m.getId();
                    starsInMoviesStatement.setString(1, starId);
                    starsInMoviesStatement.setString(2, movieId);
                    starsInMoviesStatement.addBatch();
                }
            }

            starsInMoviesStatement.executeBatch();
            connection.commit();

            String getGenreIdQuery = "SELECT max(id) as max_id FROM genres";

            PreparedStatement getGenreIdStatement = connection.prepareStatement(getGenreIdQuery);
            ResultSet getGenreIdRs = getGenreIdStatement.executeQuery();

            connection.commit();

            if (getGenreIdRs.next()) {
                int nextGenreId = getGenreIdRs.getInt("max_id") + 1;

                String query3 = "insert into genres (id, name) values (?, ?) ";
                PreparedStatement statement3 = connection.prepareStatement(query3);

                for (String genreName : newGenres) {
                    ParsedGenre g = new ParsedGenre(nextGenreId, genreName);
                    existingGenresByName.put(genreName, g);
                    statement3.setInt(1, nextGenreId);
                    statement3.setString(2, genreName);
                    statement3.addBatch();
                    nextGenreId++;
                }

                statement3.executeBatch();
                connection.commit();
            }

            String genresInMoviesQuery = "insert into genres_in_movies (genreId, movieId) values (?, ?)";
            PreparedStatement genresInMoviesStatement = connection.prepareStatement(genresInMoviesQuery);

            for (Pair gim : newGenresInMovies) {
                String genreName = gim.getFirst();
                String movieTDY = gim.getSecond();
                ParsedGenre g = existingGenresByName.get(genreName);
                ParsedMovie m = existingMoviesByTDY.get(movieTDY);
                if (g != null && m != null) {
                    int genreId = g.getId();
                    String movieId = m.getId();
                    genresInMoviesStatement.setInt(1, genreId);
                    genresInMoviesStatement.setString(2, movieId);
                    genresInMoviesStatement.addBatch();
                }
            }

            genresInMoviesStatement.executeBatch();
            connection.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        parseXmlFile("stanford-movies/mains243.xml");
        parseMainDocument();

        parseXmlFile("stanford-movies/actors63.xml");
        parseActorsDocument();

        parseXmlFile("stanford-movies/casts124.xml");
        parseCastsDocument();

        printData();
        System.out.println("\nParsed is done running.");
    }

    private void parseXmlFile(String file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.parse(file);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseMainDocument() {
        Element docEle = dom.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                addDirectorMovies(el);
            }
        }
    }

    private void addDirectorMovies(Element el) {
        String director = getTextValue(el, "dirname");

        NodeList filmElList = el.getElementsByTagName("film");

        if (filmElList != null && filmElList.getLength() > 0) {
            for (int i = 0; i < filmElList.getLength(); i++) {
                Element filmEl = (Element) filmElList.item(i);

                ParsedMovie m = new ParsedMovie();

                String fid = "";
                try {
                    fid = getTextValue(filmEl, "fid");
                } catch (Exception e) {
                    errors.add("Error: Tried to add movie with fid value='" + e.getMessage());
                }

                if (director != null) {
                    m.setDirector(director);
                } else {
                    errors.add("Error: Director value ='null' for movie " + fid);
                    continue;
                }

                try {
                    String title = getTextValue(filmEl, "t");
                    if (title.equals("NKT")) {
                        errors.add("Error: Tried to add movie with title value='NKT' for movie " + fid);
                        continue;
                    }
                    m.setTitle(title);
                } catch (Exception e) {
                    errors.add("Error: Tried to add movie with title value='" + e.getMessage() + "' for movie " + fid);
                    continue;
                }

                try {
                    int year = getIntValue(filmEl, "year");
                    m.setYear(year);
                } catch (Exception e) {
                    errors.add("Error: Tried to add movie with title year='" + e.getMessage() + "' for movie " + fid);
                }

                parseGenres(m, filmEl);

                if (existingMoviesByTDY.get(m.getTDY()) != null) {
                    errors.add("Already exists: movie with title;director;year = " + m.getTDY());
                    return;
                }
                if (newMoviesByTDY.get(m.getTDY()) != null) {
                    errors.add("Duplicate: " + fid + " has title;director;year = " + m.getTDY());
                    return;
                }
                newMoviesByTDY.put(m.getTDY(), m);
                if (fid != null) {
                    newMoviesByFID.put(fid, m);
                    newMoviesByTD.put(m.getTD(), m);
                }
            }
        }
    }

    private void parseGenres(ParsedMovie m, Element filmEl) {
        NodeList catElList = filmEl.getElementsByTagName("cat");
        if (catElList != null && catElList.getLength() > 0) {
            for (int i = 0; i < catElList.getLength(); i++) {
                Element catEl = (Element) catElList.item(i);
                try {
                    String genre = catEl.getFirstChild().getNodeValue().trim();
                    String genreFiltered = genre.substring(0, 1).toUpperCase() + genre.substring(1).toLowerCase();
                    if (existingGenresByName.get(genreFiltered) == null) {
                        newGenres.add(genreFiltered);
                        Pair genreInMovie = new Pair(genreFiltered, m.getTDY());
                        if (!existingGenresInMovies.contains(genreInMovie)) {
                            newGenresInMovies.add(genreInMovie);
                        }
                    }
                } catch (Exception e) {
                    errors.add("Error: Tried to add movie with genre value='" + e.getMessage() + "' for movie " + m.getId());
                }
            }
        }
    }


    private void parseActorsDocument() {
        Element docEle = dom.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName("actor");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                addActor(el);
            }
        }
    }

    private void addActor(Element el) {
        ParsedStar s = new ParsedStar();
        String name = getTextValue(el, "stagename");
        s.setName(name);

        try {
            int birthYear = getIntValue(el, "dob");
            s.setBirthYear(birthYear);
        } catch (Exception e) {
            if (e.getMessage() != null && !e.getMessage().equals("null")) {
                errors.add("Error: Tried to add star with dob value='" + e.getMessage() + "' for name=" + name);
            }
        }

        if (existingStarsByName.get(name) != null) {
            errors.add("Already exists: star with name=" + name);
        } else {
            newStars.put(name, s);
        }
    }


    private void parseCastsDocument() {
        Element docEle = dom.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName("dirfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                try {
                    String director = getTextValue(el, "is");
                    addDirectorFilms(el, director);
                } catch (Exception e) {

                }
            }
        }
    }

    private void addDirectorFilms(Element el, String director) {
        NodeList nl = el.getElementsByTagName("filmc");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element filmEl = (Element) nl.item(i);
                addFilmCast(filmEl, director);
            }
        }
    }

    private void addFilmCast(Element el, String director) {
        String fid = getTextValue(el, "f");
        String title = getTextValue(el, "t");
        String td = title + ";" + director;

        ParsedMovie m = newMoviesByFID.get(fid);
        if (m == null) {
            errors.add("Error: Movie with id='" + fid + "' does not exist");
            m = existingMoviesByTD.get(td);
            if (m == null) {
                m = newMoviesByTD.get(td);
                if (m == null) {
                    return;
                }
            }
        } else if (!title.equals(m.getTitle())) {
            errors.add("Error: Movie titles with fid='" + fid + "' do not match " + title + ", " + m.getTitle());
            m = existingMoviesByTD.get(td);
            if (m == null) {
                m = newMoviesByTD.get(td);
                if (m == null) {
                    return;
                }
            }
        }

        NodeList ml = el.getElementsByTagName("m");
        if (ml != null && ml.getLength() > 0) {
            for (int i = 0; i < ml.getLength(); i++) {
                Element actorEl = (Element) ml.item(i);
                try {
                    String starName = getTextValue(actorEl, "a");

                    if (newStars.get(starName) == null && existingStarsByName.get(starName) == null) {
                        errors.add("Error: Star with name='" + starName + "' does not exist");
                        continue;
                    }

                    Pair starInMovie = new Pair(starName, m.getTDY());
                    if (!existingStarsInMovies.contains(starInMovie)) {
                        newStarsInMovies.add(starInMovie);
                    } else {
                        errors.add("Already exists: " + starInMovie);
                    }
                } catch (Exception e) {
                    errors.add("Error: Tried to add movie with actor name='" + e.getMessage() + "' for movie " + fid);
                }
            }
        }
    }


    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }

    private int getIntValue(Element ele, String tagName) {
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    private void printData() {
        System.out.println("No of Existing Movies '" + existingMoviesByTDY.size() + "'.");
        System.out.println("No of Existing Stars '" + existingStarsByName.size() + "'.");
        System.out.println("No of Existing Genres '" + existingGenresByName.size() + "'.");
        System.out.println("No of Existing StarsInMovies '" + existingStarsInMovies.size() + "'.");
        System.out.println("No of Existing GenresInMovies '" + existingGenresInMovies.size() + "'.");
        System.out.println("No of New Movies '" + newMoviesByFID.size() + "'.");
        System.out.println("No of New Stars '" + newStars.size() + "'.");
        System.out.println("No of New Genres '" + newGenres.size() + "'.");
        System.out.println("No of New StarsInMovies '" + newStarsInMovies.size() + "'.");
        System.out.println("No of New GenresInMovies '" + newGenresInMovies.size() + "'.");
        System.out.println("\nInconsistencies:");
        for (String i : errors) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        MyDOMParser parser = new MyDOMParser();
        parser.loadExisting();
        parser.run();
        parser.addNew();
    }

}