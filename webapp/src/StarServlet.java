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

@WebServlet(name = "StarServlet", urlPatterns = "/api/star")
public class StarServlet extends HttpServlet {

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

            String query = "SELECT s.id, s.name, s.birthYear, GROUP_CONCAT(CONCAT(m.title, ',', m.id) SEPARATOR ';') as movies " +
                "FROM stars s, stars_in_movies sm, movies m " +
                "WHERE s.id = sm.starId " +
                "AND sm.movieId = m.id " +
                "AND s.id = ? ";

            PreparedStatement select = connection.prepareStatement(query);

            select.setString(1, id);

            ResultSet result = select.executeQuery();

            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("prevParams", prevParams);

            // print table content
            if (result.next()) {
                String star_id = result.getString("id");
                String star_name = result.getString("name");
                String star_birthyear = result.getString("birthYear");
                String star_movies = result.getString("movies");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", star_id);
                jsonObject.addProperty("star_name", star_name);
                jsonObject.addProperty("star_birthyear", formatBirthYear(star_birthyear));
                JsonArray moviesArray = getMoviesArray(star_movies);
                jsonObject.add("star_movies", moviesArray);

                jsonObj.add("data", jsonObject);
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

    protected String formatBirthYear(String birthYear) {
        if (birthYear == null) {
            return "N/A";
        }
        else {
            return birthYear;
        }
    }

    protected JsonArray getMoviesArray(String movies) {
        JsonArray jsonArray = new JsonArray();
        String[] moviesArray = movies.split(";");
        for (int i = 0; i < moviesArray.length; i++) {
            String movie = moviesArray[i];
            JsonObject jsonObject = new JsonObject();
            String[] titleAndId = movie.split(",");
            String movie_id = titleAndId[1];
            String movie_title = titleAndId[0];
            jsonObject.addProperty("movie_id", movie_id);
            jsonObject.addProperty("movie_title", movie_title);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
