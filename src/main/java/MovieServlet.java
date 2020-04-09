import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/movie")
public class MovieServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // connect to the database
            Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            // create and execute a SQL statement
            Statement select = connection.createStatement();
            String query = "SELECT mg.title, mg.year, mg.director, mg.ratings, mg.genres, GROUP_CONCAT(CONCAT(s.name, ',', s.id) SEPARATOR ';') as stars " +
                    "FROM (SELECT mr.id, mr.title, mr.year, mr.director, mr.ratings, GROUP_CONCAT(g.name SEPARATOR ';') as genres " +
                    "FROM (SELECT m.id, m.title, m.year, m.director, r.ratings " +
                    "FROM movies m, ratings r " +
                    "WHERE r.movieId = m.id AND id = '" + req.getParameter("id") +
                    "' ORDER BY r.ratings " +
                    "DESC LIMIT 20) as mr, genres_in_movies gm, genres g " +
                    "WHERE mr.id = gm.movieId " +
                    "AND gm.genreId = g.id " +
                    "GROUP BY mr.id, mr.ratings) as mg, stars_in_movies sm, stars s " +
                    "WHERE mg.id = sm.movieId " +
                    "AND sm.starId = s.id " +
                    "GROUP BY mg.id, mg.ratings " +
                    "ORDER BY mg.ratings DESC";

            ResultSet result = select.executeQuery(query);

            ResultSetMetaData metadata = result.getMetaData();

            out.println("<html>");
            out.println("<head>" +
                    "<title>Fabflix</title>" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"movies.css\">" +
                    "</head>");
            out.println("<body>");
            out.println("<h1 align=\"center\">Movie Details</h1>");

            out.println("<table align=\"center\">");

            // print table headers
            out.println("<tr>");
            out.println("<th>" + "Title" + "</th>");
            out.println("<th>" + "Year" + "</th>");
            out.println("<th>" + "Director" + "</th>");
            out.println("<th>" + "Ratings" + "</th>");
            out.println("<th>" + "Genres" + "</th>");
            out.println("<th>" + "Stars" + "</th>");
            out.println("<tr/>");

            // print table content
            while (result.next()) {
                out.println("<tr>");
                out.println("<td>" + result.getString("title") + "</td>");
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
            // back button to movie list
            out.println("<form action=\"movies\" method=\"get\" align=\"center\">");
            out.println("<input type=\"submit\" value=\"Back to movie list.\">");
            out.println("</form>");
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
        for (String genre : genres.split(";")) {
            out.println(genre + "<br>");
        }
        out.println("</td>");
    }

    protected void printStars(PrintWriter out, String stars) {
        out.println("<td>");
        for (String star : stars.split(";")) {
            String[] nameAndId = star.split(",");
            out.println("<form action=\"star\" method=\"get\">");
            out.println("<input type=\"hidden\" name=\"id\" value=\"" + nameAndId[1] + "\">");
            out.println("<input type=\"submit\" value=\"" + nameAndId[0] + "\">");
            out.println("</form>");
        }
        out.println("</td>");
    }
}
