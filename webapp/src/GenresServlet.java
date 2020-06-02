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

@WebServlet(name = "GenresServlet", urlPatterns = "/api/genres")
public class GenresServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws
            ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            String query = "SELECT * FROM genres";
            PreparedStatement select = connection.prepareStatement(query);

            ResultSet result = select.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (result.next()) {
                String genre_id = result.getString("id");
                String genre_name = result.getString("name");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genre_id", genre_id);
                jsonObject.addProperty("genre_name", genre_name);

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
        out.close();
    }
}
