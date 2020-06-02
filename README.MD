- # General
    - #### Team#: 91
    
    - #### Names: Kevin Norgaard
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:
        1. Run "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91"
        2. Add RecaptchaConstants.java file to webapp/src/main/java/
        3. In the root directory, run "mvn clean package" to compile & install dependencies
        4. Run "cp ./target/*.war /home/ubuntu/tomcat/webapps"
        5. Build and deploy APK for Android app in mobile/ directory

    - #### Collaborations and Work Distribution:
       Kevin Norgaard:
        - ##### List here

- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
        - CartServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/CartServlet.java
        - DashboardLoginServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/DashboardLoginServlet.java
        - DashboardServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/DashboardServlet.java
        - GenresServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/GenresServlet.java
        - LoginServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/LoginServlet.java
        - MetadataServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/MetadataServlet.java
        - MovieServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/MovieServlet.java
        - MoviesServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/MoviesServlet.java
        - PaymentServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/PaymentServlet.java
        - StarServlet.java https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-91/blob/master/webapp/src/main/java/StarServlet.java
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
        - The MySQL DB data source is looked up from the Context (defined in context.xml). Then from the data source, we can get a connection to query the database.
        - Connection Pooling sets the maximum number of connections (100), maximum idle connections allowed (30), and a timeout for waiting (10 sec). 
        - When getConnection() is called, pre-created connection is used upon lookup.
        - When connection.close() is called, the connections is placed back into the pool.
        
    - #### Explain how Connection Pooling works with two backend SQL.
        - Two SQL backends can be defined in context.xml. They can both be fetched by Resource name, using envContext.lookup(resourceName).
       

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |