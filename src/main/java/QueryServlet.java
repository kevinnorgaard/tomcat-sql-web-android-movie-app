import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/movies")
public class QueryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String id = req.getParameter("id");
        out.println("Servlet ID parameter: " + id);
        out.println();

        try {
            // Incorporate mySQL driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            if (connection != null) {
                System.out.println("Connection established!!");
                System.out.println();
            }

            // Create an execute an SQL statement
            Statement select = connection.createStatement();
            String query = "SELECT * FROM movies";
            if (id != "") {
                query += " WHERE id = '" + id + "'";
            }
            ResultSet result = select.executeQuery(query);

            // Get metatdata from stars; print # of attributes in table
            System.out.println("The results of the query");
            ResultSetMetaData metadata = result.getMetaData();
            System.out.println("There are " + metadata.getColumnCount() + " columns");

            out.println("<html>");
            out.println("<head><title>Fabflix</title></head>");
            out.println("<body>");
            out.println("<h1>Movies</h1>");

            out.println("<table border>");

            // Print type of each attribute
            out.println("<tr>");
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                out.println("<td>" + metadata.getColumnName(i) + "</td>");
            }
            out.println("<tr/>");

            // print table's contents, field by field
            while (result.next()) {
                out.println("<tr>");
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    out.println("<td>" + result.getString(metadata.getColumnName(i)) + "</td>");
                }
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body>");

            result.close();
            select.close();
            connection.close();
        }
        catch (Exception e) {

        }
    }
}
