import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "MovieServlet", urlPatterns = "/api/movie")
public class MovieServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");

        String id = req.getParameter("id");
        String prevParams = (String) req.getSession().getAttribute("prevParams");

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            String query = "SELECT mg.id, mg.title, mg.year, mg.director, mg.ratings, mg.genres, GROUP_CONCAT(CONCAT(s.name, ',', s.id, ',', s.count) SEPARATOR ';') as stars " +
                "FROM (" +
                "    SELECT mr.id, mr.title, mr.year, mr.director, mr.ratings, GROUP_CONCAT(g.name SEPARATOR ';') as genres " +
                "    FROM (" +
                "        SELECT m.id, m.title, m.year, m.director, r.ratings " +
                "        FROM movies m LEFT JOIN ratings r " +
                "        ON r.movieId = m.id " +
                "        WHERE id = ?" +
                "    ) as mr, genres_in_movies gm, genres g " +
                "    WHERE mr.id = gm.movieId " +
                "    AND gm.genreId = g.id " +
                "    GROUP BY mr.id, mr.ratings " +
                ") as mg, ( " +
                "    SELECT s.id, s.name, count(s.id) as count " +
                "    FROM stars s, stars_in_movies sm " +
                "    WHERE s.id = sm.starId " +
                "    GROUP BY s.id " +
                ") s, stars_in_movies sm " +
                "WHERE mg.id = sm.movieId " +
                "AND sm.starId = s.id " +
                "GROUP BY mg.id, mg.ratings ";

            PreparedStatement select = connection.prepareStatement(query);

            select.setString(1, id);

            ResultSet result = select.executeQuery();

            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("prevParams", prevParams);

            if (result.next()) {
                String movie_id = result.getString("id");
                String movie_title = result.getString("title");
                String movie_year = result.getString("year");
                String movie_director = result.getString("director");
                String movie_ratings = result.getString("ratings");
                String movie_genres = result.getString("genres");
                String movie_stars = result.getString("stars");

                JsonObject dataObj = new JsonObject();
                dataObj.addProperty("movie_id", movie_id);
                dataObj.addProperty("movie_title", movie_title);
                dataObj.addProperty("movie_year", movie_year);
                dataObj.addProperty("movie_director", movie_director);
                dataObj.addProperty("movie_ratings", formatRating(movie_ratings));
                JsonArray genresArray = genresToArray(movie_genres);
                dataObj.add("movie_genres", genresArray);
                JsonArray starArray = starsToArray(movie_stars);
                dataObj.add("movie_stars", starArray);

                jsonObj.add("data", dataObj);
            }
            out.write(jsonObj.toString());

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

    protected String formatRating(String rating) {
        if (rating == null) {
            return "N/A";
        }
        else {
            return rating;
        }
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
        for (int i = 0; i < starsArray.length; i++) {
            String star = starsArray[i];
            JsonObject jsonObject = new JsonObject();
            String[] nameAndId = star.split(",");
            String star_name = nameAndId[0];
            String star_id = nameAndId[1];
            int star_feature_count = Integer.parseInt(nameAndId[2]);
            jsonObject.addProperty("star_id", star_id);
            jsonObject.addProperty("star_name", star_name);
            jsonObject.addProperty("star_feature_count", star_feature_count);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
