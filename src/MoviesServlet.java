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

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");

        PrintWriter out = res.getWriter();

        try {
            // connect to the database
            Connection connection = dataSource.getConnection();

            // create and execute a SQL statement
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

            ResultSetMetaData metadata = result.getMetaData();

            out.println("<html>");
            out.println("<head>" +
                    "<title>Fabflix</title>" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">" +
                    "</head>");
            out.println("<body>");
            out.println("<h1>Movies</h1>");

            out.println("<table>");

            // print table headers
            out.println("<tr>");
            out.println("<th>" + "Title" + "</th>");
            out.println("<th>" + "Year" + "</th>");
            out.println("<th>" + "Director" + "</th>");
            out.println("<th>" + "Rating" + "</th>");
            out.println("<th>" + "Genres" + "</th>");
            out.println("<th>" + "Stars" + "</th>");
            out.println("<tr/>");

            int counter = 1;
            // print table content
            while (result.next()) {
                String id = result.getString("id");
                String title = result.getString("title");
                out.println("<tr>");
                out.println("<td><a href=\"movie?id=" + id + "\">" + counter++ + ". " + title + "</a></td>");
                out.println("<td>" + result.getInt("year") + "</td>");
                out.println("<td>" + result.getString("director") + "</td>");
                out.println("<td>" + result.getFloat("ratings") + "</td>");
                String genres = result.getString("genres");
                printGenres(out, genres);
                String stars = result.getString("stars");
                printStars(out, stars);
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body>");

            result.close();
            select.close();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();

            out.println("<body>");
            out.println("<p>");
            out.println("Exception in doGet: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }

        out.println("</html>");
        out.close();
    }

    protected void printGenres(PrintWriter out, String genres) {
        out.println("<td>");
        int count = 0;
        for (String genre : genres.split(";")) {
            if (count >= 3) {
                break;
            }
            out.println(genre + "<br>");
            count++;
        }
        out.println("</td>");
    }

    protected void printStars(PrintWriter out, String stars) {
        out.println("<td>");
        int count = 0;
        for (String star : stars.split(";")) {
            if (count >= 3) {
                break;
            }
            String[] nameAndId = star.split(",");
            String starId = nameAndId[1];
            String starName = nameAndId[0];
            out.println("<a href=\"star?id=" + starId + "\">" + starName + "</a><br>");
            count++;
        }
        out.println("</td>");
    }
}
