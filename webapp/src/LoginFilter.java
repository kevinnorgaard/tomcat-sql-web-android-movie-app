import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();
    private final ArrayList<String> employeeURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        Object loggedIn = httpRequest.getSession().getAttribute("loggedIn");
        boolean isLoggedIn = loggedIn != null && (boolean)loggedIn;
        Object user = httpRequest.getSession().getAttribute("user");
        boolean isCustomer = user != null && user.getClass().getName().equals("User");
        boolean isEmployee = user != null && user.getClass().getName().equals("Employee");

        if (this.isEmployeeUrl(httpRequest.getRequestURI())) {
            if (isLoggedIn && isEmployee) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect("login.html");
            }
            return;
        }

        if (isLoggedIn && isCustomer) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect("login.html");
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    private boolean isEmployeeUrl(String requestURI) {
        return employeeURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("index.html");
        allowedURIs.add("index.js");
        allowedURIs.add("style.css");
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("_dashboard");
        allowedURIs.add("_dashboard.html");
        allowedURIs.add("_dashboard.js");
        allowedURIs.add("api/dashboard-login");

        employeeURIs.add("dashboard.html");
        employeeURIs.add("dashboard.js");
        employeeURIs.add("api/metadata");
        employeeURIs.add("api/dashboard");
    }

    public void destroy() {
    }

}