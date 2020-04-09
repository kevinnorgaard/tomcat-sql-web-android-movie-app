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
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"movies.css\">" +
                    "</head>");
            out.println("<body>");
            out.println("<h1 align=\"center\"> Star Details</h1>");

            out.println("<table align=\"center\">");

            // print table headers
            out.println("<tr>");
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                out.println("<th>" + metadata.getColumnName(i) + "</th>");
            }
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
            out.println("<form action=\"movie\" method=\"get\">");
            out.println("<input type=\"hidden\" name=\"id\" value=\"" + nameAndId[1] + "\">");
            out.println("<input type=\"submit\" value=\"" + nameAndId[0] + "\">");
            out.println("</form>");
            count++;
        }
        out.println("</td>");
    }
}
