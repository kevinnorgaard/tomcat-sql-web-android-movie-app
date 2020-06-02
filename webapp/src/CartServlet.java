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
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");

        List<Movie> cartItems = user.getCart();

        JsonArray jsonArray = listToJson(cartItems);
        out.write(jsonArray.toString());

        response.setStatus(200);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");

        String operation = request.getParameter("op");
        String id = request.getParameter("id");

        int index = user.cartIndexOf(id);

        List<Movie> cartItems = user.getCart();

        if (operation.equals("INCREMENT")) {
            Movie movie = cartItems.get(index);
            movie.updateQuantity(movie.getQuantity() + 1);
        }
        else if (operation.equals("DECREMENT")) {
            Movie movie = cartItems.get(index);
            if (movie.getQuantity() - 1 >= 1) {
                movie.updateQuantity(movie.getQuantity() - 1);
            }
        }
        else if (operation.equals("REMOVE")) {
            cartItems.remove(index);
        }
        else if (operation.equals("ADD")) {
            try {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");

                Connection connection = ds.getConnection();

                String query = "SELECT m.title FROM movies m WHERE m.id = ?";

                PreparedStatement select = connection.prepareStatement(query);

                select.setString(1, id);

                ResultSet result = select.executeQuery();

                if (result.next()) {
                    String movie_title = result.getString("title");
                    double price = 14.99;

                    if (index == -1) {
                        Movie item = new Movie(id, movie_title, price, 1);

                        synchronized (cartItems) {
                            cartItems.add(item);
                        }
                    }
                    else {
                        Movie movie = cartItems.get(index);
                        movie.updateQuantity(movie.getQuantity() + 1);
                    }
                }

                result.close();
                select.close();
                connection.close();
            }
            catch (Exception e) {
                response.setStatus(500);
            }
        }
        JsonArray jsonArray = listToJson(cartItems);
        out.write(jsonArray.toString());
        response.setStatus(200);
        out.close();
    }

    public JsonArray listToJson(List<Movie> list) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < list.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            Movie item = list.get(i);
            String id = item.getId();
            String title = item.getTitle();
            double price = item.getPrice();
            int quantity = item.getQuantity();
            jsonObject.addProperty("id", id);
            jsonObject.addProperty("title", title);
            jsonObject.addProperty("price", price);
            jsonObject.addProperty("quantity", quantity);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}