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
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/api/dashboard-login")
public class DashboardLoginServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbCon = ds.getConnection();

            String email = request.getParameter("email");
            String password = request.getParameter("password");

            String query = "SELECT * from employees where email = ? ";

            PreparedStatement statement = dbCon.prepareStatement(query);
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();


            if (rs.next()) {
                String fullName = rs.getString("fullname");
                String encryptedPassword = rs.getString("password");

                boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

                if (success) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", new Employee(email, fullName));
                    session.setAttribute("loggedIn", true);
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "Success");
                }
                else {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect email and/or password");
                }
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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", ex.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        out.close();
    }
}