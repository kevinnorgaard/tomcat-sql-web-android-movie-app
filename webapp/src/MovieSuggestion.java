import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet("/api/movie-suggestion")
public class MovieSuggestion extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        JsonArray jsonArray = new JsonArray();

        String query = request.getParameter("query");

        if (query == null || query.trim().isEmpty()) {
            out.write(jsonArray.toString());
            return;
        }

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            String[] keywords = query.trim().split("\\s+");
            for (int i = 0; i < keywords.length; i++) {
                String keyword = keywords[i];
                keywords[i] = "+" + keyword + "*";
            }

            String sqlQuery = "SELECT id, title FROM movies WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE);";

            PreparedStatement select = connection.prepareStatement(sqlQuery);

            select.setString(1, String.join(" ", keywords));

            ResultSet result = select.executeQuery();

            int count = 0;
            while (result.next() && count < 10) {
                String movieId = result.getString("id");
                String movieTitle = result.getString("title");
                jsonArray.add(generateJsonObject(movieId, movieTitle));
                count++;
            }

            result.close();
            select.close();
            connection.close();
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }
        out.write(jsonArray.toString());
        out.close();
    }

    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject dataJsonObject = new JsonObject();
        dataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", dataJsonObject);
        return jsonObject;
    }


}