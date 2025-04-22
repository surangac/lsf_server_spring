# LSF Server Spring Application Deployment Guide

## Prerequisites

### Required Software
- Java Development Kit (JDK) 21 or higher
- Oracle Database 19c or higher
- Apache ActiveMQ Artemis 2.28.0 or higher (if application is connecting to remote/OMS MQ no need then)
- Gradle 8.5 or higher (included in the project) (required only for development environments)

### System Requirements
- Minimum 4GB RAM
- 2 CPU cores
- 10GB free disk space
- Operating System: Windows 10/11, Linux, or macOS

## Installation Steps

1. **Install Java 21**
   - Download and install JDK 21 from Oracle's website or use OpenJDK
   - Set JAVA_HOME environment variable
   - Add Java to your system PATH

2. **Install Oracle Database**
   - Install Oracle Database 19c or higher
   - Create a new database instance
   - Note down the connection details (host, port, service name)

3. **Install ActiveMQ Artemis**
   - Download and install Apache ActiveMQ Artemis 2.28.0
   - Create a new broker instance
   - Configure broker security settings

## Configuration

### Application Properties
The following properties can be configured in `application.properties` or `application.yml`:

#### Database Configuration
```properties
# Oracle Database Connection
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:YOUR_SERVICE_NAME
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
```

#### JMS Configuration
```properties
# ActiveMQ Connection
spring.artemis.broker-url=tcp://localhost:61616
spring.artemis.user=admin
spring.artemis.password=admin
```

#### Application Settings
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/lsf

# Logging Configuration
logging.level.root=INFO
logging.level.com.dfn.lsf=DEBUG
logging.file.name=logs/lsf-server.log

# Global Parameters Refresh Interval (in minutes)
app.global-parameters.refresh-interval=15
```

### Environment Variables
The following environment variables can be set to override configuration:

- `JAVA_OPTS`: Additional JVM options
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (dev, prod)
- `DB_HOST`: Database host
- `DB_PORT`: Database port
- `DB_NAME`: Database service name
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password
- `JMS_BROKER_URL`: ActiveMQ broker URL
- `JMS_USER`: JMS username
- `JMS_PASSWORD`: JMS password

## Deployment

### Development Environment
1. Clone the repository
2. Run `./gradlew bootRun` or use the provided `run.bat` script

### Production Environment
1. Build the application:
   ```bash
   ./gradlew clean build
   ```
2. The JAR file will be generated in `build/libs/`
3. Run the application:
   ```bash
   java -jar build/libs/lsf-server-spring-1.0.0.jar
   ```
or use the provided `run.bat` script change the .jar file in the .bat accordingly.


### Windows Service Installation
1. Use a service wrapper like NSSM or WinSW
2. Configure the service with appropriate JVM options and environment variables

## Monitoring and Maintenance

### Health Checks
- Application health endpoint: `http://localhost:8080/lsf/actuator/health`
- Metrics endpoint: `http://localhost:8080/lsf/actuator/metrics`


## Troubleshooting

### Common Issues
1. **Database Connection Issues**
   - Verify database credentials
   - Check network connectivity
   - Ensure Oracle client is properly installed

2. **JMS Connection Issues**
   - Verify ActiveMQ is running
   - Check broker credentials
   - Ensure network ports are accessible

3. **Memory Issues**
   - Adjust JVM heap size using `-Xmx` and `-Xms` options
   - Monitor memory usage through actuator endpoints


### SSL Configuration
```properties
# SSL Configuration
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_keystore_password
server.ssl.key-store-type=PKCS12
```

## Performance Tuning

### Recommended JVM Options
```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:InitialRAMPercentage=50.0
-XX:MaxRAMPercentage=75.0
```
