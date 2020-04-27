Demo URL: https://www.youtube.com/watch?v=d149Agx4lbA

##Steps to Deploy Application:
(No deviation from technologies used in instructions)
1. Run "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91"
3. Where pom.xml is located, run "mvn package" in terminal to install dependencies with Maven
3. Run "cp ./target/*.war /home/ubuntu/tomcat/webapps"

##Substring Matching Design:
Search:
- Title: movies.title LIKE '%some-title%'
- Year: movies.year = some-year
- Director: movies.director LIKE '%some-director%'
- Star: stars.name LIKE '%some-star%' (after joining movies and stars)

Browsing:
- Genre: mg.genres LIKE '%genres%' (after joining movies and genres)
- A-Z: movies.title LIKE 'some-letter%'
- *: movies.title REGEXP '^[^a-zA-Z0-9]'

##Member Contribution:
1. Kevin Norgaard (everything)
- Login page
- Main page
- Searching
- Movie list page
- Single pages
- Jump functionality
- Browsing
- Shopping cart page
- Payment page
- Place order action
