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

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Connection dbCon = dataSource.getConnection();

            Statement statement = dbCon.createStatement();

            String email = request.getParameter("email");
            String password = request.getParameter("password");

            String query = String.format("SELECT * from customers where email = '%s' and password = '%s'", email, password);

            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                String id = rs.getString("id");
                HttpSession session = request.getSession();
                session.setAttribute("id", id);
                session.setAttribute("loggedIn", true);
                response.sendRedirect("index.html");
            }
            else {
                response.sendRedirect("login.html");
            }

            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {
        }
    }
}