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
import java.util.List;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<Sale> saleItems = user.getSales();

        JsonArray jsonArray = salesToJson(saleItems);

        out.write(jsonArray.toString());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<Movie> cartItems = user.getCart();

        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        String creditCardNumber = request.getParameter("cc-number");
        String expirationDate = request.getParameter("exp-date");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        jsonObject.addProperty("creditCardNumber", creditCardNumber);
        jsonObject.addProperty("expirationDate", expirationDate);
        jsonObject.addProperty("processed", false);

        JsonArray cartArray = cartToJson(cartItems);
        jsonObject.add("cart", cartArray);

        try {
            Connection connection = dataSource.getConnection();

            Statement select1 = connection.createStatement();
            String ccQuery = String.format(
                    "SELECT * FROM creditcards cc " +
                    "WHERE cc.id = '%s' " +
                    "AND cc.firstName = '%s' " +
                    "AND cc.lastName = '%s' " +
                    "AND cc.expiration = '%s'",
                    creditCardNumber,
                    firstName,
                    lastName,
                    expirationDate
            );

            ResultSet result1 = select1.executeQuery(ccQuery);

            if (result1.next()) {
                result1.close();
                select1.close();

                String id = user.getId();
                for (int i = 0; i < cartItems.size(); i++) {
                    Movie movie = cartItems.get(i);
                    for (int j = 0; j < movie.getQuantity(); j++) {
                        Statement select2 = connection.createStatement();
                        String saleQuery = String.format("INSERT INTO sales (id, customerId, movieId, saleDate) VALUES(NULL, '%s', '%s', curdate())",
                                id,
                                movie.getId()
                        );
                        select2.executeUpdate(saleQuery, Statement.RETURN_GENERATED_KEYS);
                        ResultSet result2 = select2.getGeneratedKeys();
                        String saleId = "";
                        if (result2.next()) {
                            saleId = result2.getString(1);
                        }
                        Sale sale = new Sale(saleId, movie.getTitle(), movie.getPrice(), 1);
                        user.addSale(sale);
                        result2.close();
                        select2.close();
                    }
                }

                jsonObject.add("sales", salesToJson(user.getSales()));
                jsonObject.addProperty("processed", true);

//                user.setSale();
            }
            else {
                result1.close();
                select1.close();
                connection.close();
            }
        }
        catch (Exception e) {
            response.setStatus(500);
        }

        out.write(jsonObject.toString());
        response.setStatus(200);
        out.close();
    }

    public JsonArray cartToJson(List<Movie> list) {
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

    public JsonArray salesToJson(List<Sale> list) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < list.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            Sale item = list.get(i);
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