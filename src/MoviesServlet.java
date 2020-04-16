import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws
            ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");

        try {
            Connection connection = dataSource.getConnection();

            Statement select = connection.createStatement();
            String query = "SELECT mg.id, mg.title, mg.year, mg.director, mg.ratings, mg.genres, GROUP_CONCAT(CONCAT(s.name, ',', s.id) SEPARATOR ';') as stars \r\n" +
                    "FROM (\r\n" +
                    "	SELECT mr.id, mr.title, mr.year, mr.director, mr.ratings, GROUP_CONCAT(g.name SEPARATOR ';') as genres \r\n" +
                    "	FROM (\r\n" +
                    "		SELECT m.id, m.title, m.year, m.director, r.ratings \r\n" +
                    "		FROM movies m, ratings r \r\n" +
                    "		WHERE r.movieId = m.id \r\n" +
                    "		ORDER BY r.ratings \r\n" +
                    "		DESC LIMIT 20) \r\n" +
                    "	mr, genres_in_movies gm, genres g \r\n" +
                    "	WHERE mr.id = gm.movieId AND gm.genreId = g.id GROUP BY mr.id, mr.ratings) \r\n" +
                    "mg, stars_in_movies sm, stars s \r\n" +
                    "WHERE mg.id = sm.movieId \r\n" +
                    "AND sm.starId = s.id \r\n" +
                    "GROUP BY mg.id, mg.ratings \r\n" +
                    "ORDER BY mg.ratings DESC";

            ResultSet result = select.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            while (result.next()) {
                String movie_id = result.getString("id");
                String movie_title = result.getString("title");
                String movie_year = result.getString("year");
                String movie_director = result.getString("director");
                String movie_ratings = result.getString("ratings");
                String movie_genres = result.getString("genres");
                String movie_stars = result.getString("stars");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_ratings", movie_ratings);
                JsonArray genresArray = genresToArray(movie_genres);
                jsonObject.add("movie_genres", genresArray);
                JsonArray starsArray = starsToArray(movie_stars);
                jsonObject.add("movie_stars", starsArray);

                jsonArray.add(jsonObject);
            }
            out.write(jsonArray.toString());

            res.setStatus(200);

            result.close();
            select.close();
            connection.close();
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            res.setStatus(500);
        }
        out.close();
    }

    protected JsonArray genresToArray(String genres) {
        JsonArray jsonArray = new JsonArray();
        String[] genresArray = genres.split(";");
        for (int i = 0; i < Math.min(3, genresArray.length); i++) {
            String genre = genresArray[i];
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("genre", genre);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    protected JsonArray starsToArray(String stars) {
        JsonArray jsonArray = new JsonArray();
        String[] starsArray = stars.split(";");
        for (int i = 0; i < Math.min(3, starsArray.length); i++) {
            String star = starsArray[i];
            JsonObject jsonObject = new JsonObject();
            String[] nameAndId = star.split(",");
            String star_id = nameAndId[1];
            String star_name = nameAndId[0];
            jsonObject.addProperty("star_id", star_id);
            jsonObject.addProperty("star_name", star_name);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
