import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/star")
public class StarServlet extends HttpServlet {
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
            String query = "SELECT s.name, s.birthYear, GROUP_CONCAT(CONCAT(m.title, ',', m.id) SEPARATOR ';') as movies FROM stars s, stars_in_movies sm, movies m WHERE s.id = sm.starId AND sm.movieId = m.id AND s.id = '" + req.getParameter("id") + "'";

            ResultSet result = select.executeQuery(query);

            ResultSetMetaData metadata = result.getMetaData();

            out.println("<html>");
            out.println("<head>" +
                    "<title>Fabflix</title>" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">" +
                    "</head>");
            out.println("<body>");
            out.println("<h1> Star Details</h1>");

            out.println("<table>");

            // print table headers
            out.println("<tr>");
            out.println("<th>" + "Name" + "</th>");
            out.println("<th>" + "Birth Year" + "</th>");
            out.println("<th>" + "Movies" + "</th>");
            out.println("<tr/>");

            // print table content
            while (result.next()) {
                out.println("<tr>");
                out.println("<td>" + result.getString("name") + "</td>");
                String birthYear = result.getString("birthYear");
                printBirthYear(out, birthYear);
                String movies = result.getString("movies");
                printMovies(out, movies);
                out.println("</tr>");
            }

            out.println("</table>");
            // back button to movie list
            out.println("<btn class=\"back-btn\"><a href=\"movies\">Back to movies</a></btn>");
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

    protected void printBirthYear(PrintWriter out, String birthYear) {
        if (birthYear == null) {
            out.println("<td>N/A</td>");
        }
        else {
            out.println("<td>" + birthYear + "</td>");
        }
    }

    protected void printMovies(PrintWriter out, String movies) {
        out.println("<td>");
        int count = 0;
        for (String movie : movies.split(";")) {
            if (count >= 3) {
                break;
            }
            String[] nameAndId = movie.split(",");
            String movieId = nameAndId[1];
            String movieName = nameAndId[0];
            out.println("<a href=\"movie?id=" + movieId + "\">" + movieName + "</a><br>");
            count++;
        }
        out.println("</td>");
    }
}
