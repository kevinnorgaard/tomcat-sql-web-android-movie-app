import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            String operation = request.getParameter("operation");

            if (operation.equals("INSERT-NEW-STAR")) {
                JsonObject jsonObject = new JsonObject();

                String getIdQuery = "SELECT id FROM stars_next_id";

                PreparedStatement getIdStatement = connection.prepareStatement(getIdQuery);
                ResultSet getIdRs = getIdStatement.executeQuery();

                if (getIdRs.next()) {
                    int nextId = getIdRs.getInt("id");

                    String updateIdQuery = "UPDATE stars_next_id SET id = ?";
                    PreparedStatement updateIdStatement = connection.prepareStatement(updateIdQuery);
                    updateIdStatement.setInt(1, nextId + 1);
                    updateIdStatement.executeUpdate();

                    String starId = "nm" + nextId;

                    String starName = request.getParameter("star-name");
                    String starBirthYear = request.getParameter("star-birthyear");

                    String query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";

                    PreparedStatement statement = connection.prepareStatement(query);

                    statement.setString(1, starId);
                    statement.setString(2, starName);

                    if (starBirthYear.equals("")) {
                        statement.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(3,  Integer.parseInt(starBirthYear));
                    }

                    statement.executeUpdate();

                    JsonObject rowObject = new JsonObject();
                    rowObject.addProperty("id", starId);
                    rowObject.addProperty("starName", starName);
                    rowObject.addProperty("starBirthYear", starBirthYear);
                    jsonObject.add("row", rowObject);

                    jsonObject.addProperty("status", "success");

                    statement.close();
                }
                out.write(jsonObject.toString());

                getIdRs.close();
                getIdStatement.close();
            }
            else if (operation.equals("INSERT-NEW-MOVIE")) {
                String movieName = request.getParameter("movie-name");
                String movieYear = request.getParameter("movie-year");
                String movieDirector = request.getParameter("movie-director");
                String starName = request.getParameter("star-name");
                String genreName = request.getParameter("genre-name");

                String query = "{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                CallableStatement statement = connection.prepareCall(query);
                statement.setString(1, movieName);
                statement.setInt(2, Integer.parseInt(movieYear));
                statement.setString(3, movieDirector);
                statement.setString(4, starName);
                statement.setString(5, genreName);
                statement.registerOutParameter(6, Types.VARCHAR);
                statement.registerOutParameter(7, Types.VARCHAR);
                statement.registerOutParameter(8, Types.INTEGER);
                statement.registerOutParameter(9, Types.BOOLEAN);
                statement.registerOutParameter(10, Types.BOOLEAN);
                statement.registerOutParameter(11, Types.BOOLEAN);
                statement.execute();

                JsonObject jsonObject = new JsonObject();
                String movieId = statement.getString(6);
                String starId = statement.getString(7);
                int genreId = statement.getInt(8);
                boolean movieAdded = statement.getBoolean(9);
                boolean starAdded = statement.getBoolean(10);
                boolean genreAdded = statement.getBoolean(11);
                jsonObject.addProperty("movie-added", movieAdded);
                jsonObject.addProperty("star-added", starAdded);
                jsonObject.addProperty("genre-added", genreAdded);
                jsonObject.addProperty("movie-id", movieId);
                jsonObject.addProperty("star-id", starId);
                jsonObject.addProperty("genre-id", genreId);
                out.write(jsonObject.toString());

                statement.close();
            }
            connection.close();
        }
        catch (Exception ex) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", ex.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        out.close();
    }
}