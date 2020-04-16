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

@WebServlet(name = "StarServlet", urlPatterns = "/api/star")
public class StarServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");

        HttpSession session = req.getSession();
        Object loggedIn = session.getAttribute("loggedIn");


        if (loggedIn != null && (boolean)loggedIn) {
            try {
                // connect to the database
                Connection connection = dataSource.getConnection();

                // create and execute a SQL statement
                Statement select = connection.createStatement();
                String query = "SELECT s.name, s.birthYear, GROUP_CONCAT(CONCAT(m.title, ',', m.id) SEPARATOR ';') as movies FROM stars s, stars_in_movies sm, movies m WHERE s.id = sm.starId AND sm.movieId = m.id AND s.id = '" + req.getParameter("id") + "'";

                ResultSet result = select.executeQuery(query);

                JsonArray jsonArray = new JsonArray();

                // print table content
                while (result.next()) {
                    String star_name = result.getString("name");
                    String star_birthyear = result.getString("birthYear");
                    String star_movies = result.getString("movies");

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("star_name", star_name);
                    jsonObject.addProperty("star_birthyear", getBirthYear(star_birthyear));
                    JsonArray moviesArray = getMoviesArray(star_movies);
                    jsonObject.add("star_movies", moviesArray);

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
        }
        else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("redirect", true);
            out.write(jsonObject.toString());
        }
        out.close();
    }

    protected String getBirthYear(String birthYear) {
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
