import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws
            ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection connection = ds.getConnection();

            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                String tableName = rs.getString(3);
                jsonObject.addProperty("tableName", tableName);

                String query = String.format("SELECT * FROM %s LIMIT 0",
                    tableName);
                PreparedStatement select = connection.prepareStatement(query);
                ResultSet result = select.executeQuery();
                ResultSetMetaData metadata = result.getMetaData();

                JsonArray columnArray = new JsonArray();

                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    String columnName = metadata.getColumnName(i);
                    String columnType = metadata.getColumnTypeName(i);
                    int columnSize = metadata.getColumnDisplaySize(i);
                    JsonObject columnObject = new JsonObject();
                    columnObject.addProperty("columnName", columnName);
                    columnObject.addProperty("columnType", columnType);
                    columnObject.addProperty("columnSize", columnSize);
                    columnArray.add(columnObject);
                }

                jsonObject.add("columns", columnArray);

                jsonArray.add(jsonObject);

                result.close();
                select.close();
            }

            out.write(jsonArray.toString());

            res.setStatus(200);

            connection.close();
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            res.setStatus(500);
        }
        out.close();
    }
}
