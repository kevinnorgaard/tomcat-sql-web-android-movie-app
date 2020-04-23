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

        req.getSession().setAttribute("prevParams", req.getQueryString());

        String title = req.getParameter("title") != null ? req.getParameter("title") : "";
        String year = req.getParameter("year") != null ? req.getParameter("year") : "";
        String director = req.getParameter("director") != null ? req.getParameter("director") : "";
        String star = req.getParameter("star") != null ? req.getParameter("star") : "";
        String genre = req.getParameter("genre") != null ? req.getParameter("genre") : "";
        String titleStart = req.getParameter("titlestart") != null ? req.getParameter("titlestart") : "";
        String limit = req.getParameter("limit") != null ? req.getParameter("limit") : "";
        String offset = req.getParameter("offset") != null ? req.getParameter("offset") : "";
        String psort = req.getParameter("psort") != null ? req.getParameter("psort") : "";
        String ssort = req.getParameter("ssort") != null ? req.getParameter("ssort") : "";

        try {
            Connection connection = dataSource.getConnection();

            Statement select = connection.createStatement();
            String query = String.format(
                "SELECT mg.id, mg.title, mg.year, mg.director, mg.ratings, mg.genres, GROUP_CONCAT(CONCAT(s.name, ',', s.id, ',', s.count) SEPARATOR ';') as stars " +
                "FROM (" +
                "    SELECT mr.id, mr.title, mr.year, mr.director, mr.ratings, GROUP_CONCAT(g.name SEPARATOR ';') as genres " +
                "    FROM (" +
                "        SELECT m.id, m.title, m.year, m.director, r.ratings " +
                "        FROM movies m, ratings r " +
                "        WHERE r.movieId = m.id " +
                "        %s " +
                "        %s " +
                "        %s " +
                "        %s " +
                "        ORDER BY r.ratings " +
                "        DESC " +
                "    ) mr, genres_in_movies gm, genres g " +
                "    WHERE mr.id = gm.movieId " + "" +
                "    AND gm.genreId = g.id " +
                "    GROUP BY mr.id, mr.ratings" +
                ") mg, (" +
                "    SELECT s.id, s.name, count(s.id) as count " +
                "    FROM stars s, stars_in_movies sm " +
                "    WHERE s.id = sm.starId " +
                "    GROUP BY s.id " +
                ") s, stars_in_movies sm " +
                "WHERE mg.id = sm.movieId " +
                "AND sm.starId = s.id " +
                "%s " +
                "%s " +
                "GROUP BY mg.id, mg.ratings " +
                "ORDER BY %s %s " +
                "LIMIT %s " +
                "OFFSET %s ",
                    !title.equals("") ? "AND m.title LIKE '%" + title + "%'" : "",
                    !titleStart.equals("") ? (titleStart.equals("*") ? "AND m.title REGEXP '^[^a-zA-Z0-9]'" : "AND m.title LIKE '" + titleStart + "%'") : "",
                    !year.equals("") ? "AND m.year = " + year : "",
                    !director.equals("") ? "AND m.director LIKE '%" + director + "%'" : "",
                    !star.equals("") ? "AND s.name LIKE '%" + star + "%'" : "",
                    !genre.equals("") ? "AND genres LIKE '%" + genre + "%'" : "",
                    !psort.equals("") ? getSortQuery(psort) : "mg.ratings DESC",
                    !ssort.equals("") ? "," + getSortQuery(ssort) : "",
                    !limit.equals("") ? limit : "10",
                    !offset.equals("") ? offset : "0"
            );

            ResultSet result = select.executeQuery(query);

            JsonObject jsonObj = new JsonObject();

            jsonObj.addProperty("offset", offset);
            jsonObj.addProperty("limit", limit);
            jsonObj.addProperty("psort", psort);
            jsonObj.addProperty("ssort", ssort);

            JsonArray dataArray = new JsonArray();
            while (result.next()) {
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
                dataObj.addProperty("movie_ratings", movie_ratings);
                JsonArray genresArray = genresToArray(movie_genres);
                dataObj.add("movie_genres", genresArray);
                JsonArray starsArray = starsToArray(movie_stars);
                dataObj.add("movie_stars", starsArray);

                dataArray.add(dataObj);
            }
            jsonObj.add("data", dataArray);

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
            JsonObject jsonObj = new JsonObject();
            String[] nameAndId = star.split(",");
            String star_name = nameAndId[0];
            String star_id = nameAndId[1];
            int star_feature_count = Integer.parseInt(nameAndId[2]);
            jsonObj.addProperty("star_name", star_name);
            jsonObj.addProperty("star_id", star_id);
            jsonObj.addProperty("star_feature_count", star_feature_count);
            jsonArray.add(jsonObj);
        }
        return jsonArray;
    }

    protected String getSortQuery(String sort) {
        if (sort.equals("title-asc")) {
            return "mg.title ASC";
        }
        else if (sort.equals("title-desc")) {
            return "mg.title DESC";
        }
        else if (sort.equals("rating-asc")) {
            return "mg.ratings ASC";
        }
        else if (sort.equals("rating-desc")) {
            return "mg.ratings DESC";
        }
        else {
            return "";
        }
    }
}
