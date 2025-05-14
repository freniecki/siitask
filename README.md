# SII Internship Recruitment Task 
## Author: Franciszek Reniecki

Presented app is part of recruitment process for SII company.

Source code for this app is available at: [GitHub](https://github.com/freniecki/siitask)

---

### How to run:
#### Requirements:
* SDK Java 21 (preferably OpenJDK)
* for external exchange rates:
  * _secret.properties_ file is required in _resources_ directory (attached to .zip file and additionally send via email)
  * if file is not found, app will use default rates

For local usage please run the following command in project root directory:

`
mvn clean install && java -jar target/siitask-0.0.1-SNAPSHOT.jar
`

### REST API documentation:

[OpenAPI](http://localhost:8080/swagger-ui/index.html) (_after starting the app_)

