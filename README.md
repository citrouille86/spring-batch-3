# SpringBatch + SpringBoot

SpringBatch Fwk with SpringBoot

# Performance tip
To write lots of data and to protect against getParameterType cursor leak with JDBC driver, fix a system property (in spring.properties file for example)  _spring.jdbc.getParameterType.ignore_  to true
