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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("userAgent", request.getHeader("User-Agent"));
        if (!request.getHeader("User-Agent").equals("Dalvik/2.1.0 (Linux; U; Android R Build/RPP4.200409.015)")) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

            // Verify reCAPTCHA
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                jsonObject.addProperty("gRecatchaError", e.getMessage());
                out.write(jsonObject.toString());
                out.close();
                return;
            }
        }

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbCon = ds.getConnection();

            String email = request.getParameter("email");
            String password = request.getParameter("password");

            String query = "SELECT * from customers where email = ? ";

            PreparedStatement statement = dbCon.prepareStatement(query);
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id");
                String encryptedPassword = rs.getString("password");

                boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

                if (success) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", new User(id, email));
                    session.setAttribute("loggedIn", true);
                    jsonObject.addProperty("status", "success");
                    jsonObject.addProperty("message", "Success");
                }
                else {
                    jsonObject.addProperty("status", "fail");
                    jsonObject.addProperty("message", "Incorrect email and/or password");
                }
            }
            else {
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("message", "Incorrect email and/or password");
            }
            out.write(jsonObject.toString());

            rs.close();
            statement.close();
            dbCon.close();
        }
        catch (Exception ex) {
            jsonObject.addProperty("errorMessage", ex.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        out.close();
    }
}