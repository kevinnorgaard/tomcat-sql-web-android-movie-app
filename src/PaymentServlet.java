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

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        List<Movie> cartItems = user.getCart();

        for (int i = 0; i < cartItems.size(); i++) {
            Movie movie = cartItems.get(i);
        }

        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        String creditCardNumber = request.getParameter("cc-number");
        String expirationDate = request.getParameter("exp-date");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        jsonObject.addProperty("creditCardNumber", creditCardNumber);
        jsonObject.addProperty("expirationDate", expirationDate);
        jsonObject.addProperty("valid", false);

        JsonArray cartArray = listToJson(cartItems);
        jsonObject.add("cart", cartArray);

        try {
            Connection connection = dataSource.getConnection();

            Statement select = connection.createStatement();
            String query = String.format(
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

            ResultSet result = select.executeQuery(query);

            if (result.next()) {
                jsonObject.addProperty("valid", true);
            }

            result.close();
            select.close();
            connection.close();
        }
        catch (Exception e) {
            response.setStatus(500);
        }

        out.write(jsonObject.toString());
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