Demo URL: 

##Steps to Deploy Application:
(No deviation from technologies used in instructions)
1. Run "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91"
3. Where pom.xml is located, run "mvn package" in terminal to install dependencies with Maven
3. Run "cp ./target/*.war /home/ubuntu/tomcat/webapps"

##Parsing Time Optimization Strategies:
1. Load all Movies, Stars, Genres, StarsInMovies, and GenresInMovies into a HashMap or HashSet. Add all new movies from mains.xml to a HashMap by "fid" to quickly fetch when parsing casts.xml.
2. Use PreparedStatement.addBatch() and PreparedStatement.executeBatch() to update MySQL database.

##Member Contribution:
1. Kevin Norgaard (everything)
- reCAPTCHA
- HTTPS
- Password Encryption
- PreparedStatement for all queries
- Employee Dashboard
- XML Parsing
