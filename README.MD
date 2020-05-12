Demo URL: https://www.youtube.com/watch?v=nm3WtjMUrpU

##Steps to Deploy Application:
(No deviation from technologies used in instructions)
1. Run "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91"
2. Add RecaptchaConstants.java file to webapp/src/main/java/
3. In the root directory, run "mvn clean package" to compile & install dependencies
4. Run "cp ./target/*.war /home/ubuntu/tomcat/webapps"

##Parsing Time Optimization Strategies:
1. Load all Movies, Stars, Genres, StarsInMovies, and GenresInMovies into a HashMap or HashSet. Add all new movies from mains.xml to a HashMap by "fid" to quickly fetch when parsing casts.xml.
2. Use PreparedStatement.addBatch() and PreparedStatement.executeBatch() to update MySQL database.

##PreparedStatements:
1. CartServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/CartServlet.java
2. DashboardLoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/DashboardLoginServlet.java
3. DashboardServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/DashboardServlet.java
4. GenresServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/GenresServlet.java
5. LoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/LoginServlet.java
6. MetadataServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/MetadataServlet.java
7. MovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/MovieServlet.java
8. MoviesServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/MoviesServlet.java
9. PaymentServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/PaymentServlet.java
10. StarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/StarServlet.java

##Inconsistencies from XML Parsing:
See inconsistencies-output.txt file in root for parsing output.

##Member Contribution:
1. Kevin Norgaard (everything)
- reCAPTCHA
- HTTPS
- Password Encryption
- PreparedStatement for all queries
- Employee Dashboard
- XML Parsing
