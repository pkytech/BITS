# BITS

A directory which holds the Proof Of Concept (PoC) code for BITS Dissertation pertaining to author.

**## Prerequisite:**
1. Any Linux OS
2. JDK 1.8 - installed and set it in PATH and set JAVA_HOME
3. Install and Configure Kafka as mentioned in https://kafka.apache.org/quickstart
4. Create Kafka topic called 'csp'
5. Install and configure maven

**## Steps for starting servers and running tests:**
1. Open `BITS/dissertation/common/src/main/resources/bit-config.properties` file in editor and configure `**bits.mtech.kafka.server**` to a server and port of kafka zookeeper server
2. Open command prompt and go to folder `BITS/dissertation/common` and execute command `mvn clean compile install`
3. After successful build of common component, go to `BITS/dissertation/microservices` folder and build all microservices using command `mvn clean compile package`. After successful build starts servers
4. Start servers in following order using command (in sequence)
* eureka-server - Goto `BITS/dissertation/eureka-server` and start using command `java -jar target/eureka-server-1.0.0-SNAPSHOT.jar`
* acquirer - Goto `BITS/dissertation/microservices/acquirer` and start using command `java -jar target/acquirer-service-1.0.0-SNAPSHOT.jar`
* Payment - Goto `BITS/dissertation/microservices/payment` and start using command `java -jar target/payment-service-1.0.0-SNAPSHOT.jar`
* Order - Goto `BITS/dissertation/microservices/order` and start using command `java -jar target/order-service-1.0.0-SNAPSHOT.jar`
* Billing - Goto `BITS/dissertation/microservices/billing` and start using command `java -jar target/billing-service-1.0.0-SNAPSHOT.jar`
5. Then open http://localhost:8999/ in browser and verify that all serves are registered in Eureka=server
6. Modify `BITS/dissertation/FlowTest/src/test/resources/bits-test-config.properties` file to update server ip and port pertaining to payment, order, bill microservices respectively.

`bits.mtech.payment.server=http://192.168.0.7:8081` and
`bits.mtech.order.server=http://192.168.0.7:8084` and 
`bits.mtech.bill.server=http://192.168.0.7:8082`
7. Open command prompt `BITS/dissertation/FlowTest` and run tests using command `mvn clean compile test`. The test result report will be created in target folder `BITS/dissertation/FlowTest/target/surefire-reports/emailable-report.html`. 

If all three tests are successful then complete configuration and process is successful.

