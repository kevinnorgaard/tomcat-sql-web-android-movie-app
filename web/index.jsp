<%@ page import="edu.uci.cs122b.*" %><%--
  Created by IntelliJ IDEA.
  User: Kevin
  Date: 4/3/20
  Time: 10:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$TITLE$</title>
  </head>
  <body>
    <form action="movies" method="get">
      Query: <input type="text" name="query">
      <br>
      <input type="submit" value="Show Movie List">
    </form>
  </body>
</html>