package edu.uci.cs122b;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/movies")
public class QueryServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        try {
            this.query(req, res);
            out.println("Finished Query");
        }
        catch (Exception e) {
            out.println(e.toString());
        }
    }

    public void query(HttpServletRequest req, HttpServletResponse res) throws Exception {
        PrintWriter out = res.getWriter();

        String parameter1 = req.getParameter("query");
        out.println("Servlet query parameter: " + parameter1);
        out.println();

        // Incorporate mySQL driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        // Connect to the database
        Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                Parameters.username, Parameters.password);

        if (connection != null) {
            System.out.println("Connection established!!");
            System.out.println();
        }

        // Create an execute an SQL statement
        Statement select = connection.createStatement();
        String query = "SELECT * FROM movies";
        if (parameter1 != "") {
            query += " WHERE id = '" + parameter1 + "'";
        }
        ResultSet result = select.executeQuery(query);

        // Get metatdata from stars; print # of attributes in table
        System.out.println("The results of the query");
        ResultSetMetaData metadata = result.getMetaData();
        System.out.println("There are " + metadata.getColumnCount() + " columns");

        // Print type of each attribute
//        for (int i = 1; i <= metadata.getColumnCount(); i++) {
//            out.println("Type of column " + i + " is " + metadata.getColumnTypeName(i));
//        }

        // print table's contents, field by field
        while (result.next()) {
            out.println("Id = " + result.getString("id"));
            out.println("Title = " + result.getString("title"));
            out.println("Year = " + result.getInt("year"));
            out.println("Director = " + result.getString("director"));
            out.println();
        }

        connection.close();
    }
}
