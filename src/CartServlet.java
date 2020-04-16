import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HttpSession session = request.getSession();
//        String sessionId = session.getId();
//        long lastAccessTime = session.getLastAccessedTime();
//
//        JsonObject responseJsonObject = new JsonObject();
//        responseJsonObject.addProperty("sessionID", sessionId);
//        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
//
//        // write all the data into the jsonObject
//        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        List<Movie> cartItems = user.getCart();

        String id = request.getParameter("id");
        String quantity = request.getParameter("quantity");

        try {
            Connection connection = dataSource.getConnection();

            Statement select = connection.createStatement();
            String query = "SELECT m.title FROM movies m WHERE m.id = '" + id + "'";

            ResultSet result = select.executeQuery(query);

            if (result.next()) {
                String movie_title = result.getString("title");

                Movie item = new Movie(movie_title, 14.34, Integer.parseInt(quantity));

                synchronized (cartItems) {
                    cartItems.add(item);
                }

                JsonArray jsonArray = listToJson(cartItems);
                out.write(jsonArray.toString());

                response.setStatus(200);
            }

            result.close();
            select.close();
            connection.close();
        }
        catch (Exception e) {
            response.setStatus(500);
        }
        out.close();
    }

    public JsonArray listToJson(List<Movie> list) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < list.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            Movie item = list.get(i);
            String title = item.getTitle();
            double price = item.getPrice();
            int quantity = item.getQuantity();
            jsonObject.addProperty("title", title);
            jsonObject.addProperty("price", price);
            jsonObject.addProperty("quantity", quantity);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}