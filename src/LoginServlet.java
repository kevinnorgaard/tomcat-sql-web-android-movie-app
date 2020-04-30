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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        try {
            Connection dbCon = dataSource.getConnection();

            String email = request.getParameter("email");
            String password = request.getParameter("password");

            String query = "SELECT * from customers where email = ? and password = ?";

            PreparedStatement statement = dbCon.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            if (rs.next()) {
                String id = rs.getString("id");
                HttpSession session = request.getSession();
                session.setAttribute("user", new User(id, email));
                session.setAttribute("loggedIn", true);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Success");
            }
            else {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Incorrect email and/or password");
            }
            out.write(responseJsonObject.toString());

            rs.close();
            statement.close();
            dbCon.close();
        }
        catch (Exception ex) {
        }
        out.close();
    }
}