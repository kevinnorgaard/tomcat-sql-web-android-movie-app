import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/movies")
public class MoviesServlet extends HttpServlet {
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
            String query = "SELECT * FROM movies";

            ResultSet result = select.executeQuery(query);

            ResultSetMetaData metadata = result.getMetaData();

            out.println("<html>");
            out.println("<head>" +
                    "<title>Fabflix</title>" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"movies.css\">" +
                    "</head>");
            out.println("<body>");
            out.println("<h1>Movies</h1>");

            out.println("<table>");

            // print table headers
            out.println("<tr>");
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                out.println("<th>" + metadata.getColumnName(i) + "</th>");
            }
            out.println("<tr/>");

            // print table content
            while (result.next()) {
                out.println("<tr>");
                out.println("<form action=\"movie\" method=\"get\">");
                out.println("<input type=\"hidden\" name=\"id\" value=\"" + result.getString("id") + "\">");
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    out.println("<td>" + result.getString(metadata.getColumnName(i)) + "</td>");
                }
                out.println("<td><input type=\"submit\" value=\"Show Movie\"></td>");
                out.println("</form>");
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
}
