# Check if Java is installed
$javaVersion = java -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Java is not installed. Please run setup_environment.ps1 first."
    exit 1
}

# Check if Maven is installed
$mvnVersion = mvn -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Maven is not installed. Please run setup_environment.ps1 first."
    exit 1
}

# Create Maven wrapper if it doesn't exist
if (-not (Test-Path "mvnw.cmd")) {
    Write-Host "Creating Maven wrapper..."
    mvn -N wrapper:wrapper
}

# Clean and install dependencies
Write-Host "Cleaning and installing dependencies..."
./mvnw clean install -DskipTests

# Create application.properties if it doesn't exist
$propertiesPath = "src/main/resources/application.properties"
if (-not (Test-Path $propertiesPath)) {
    Write-Host "Creating application.properties..."
    @"
# Server Configuration
server.port=5000
server.servlet.context-path=/
server.error.whitelabel.enabled=false
server.address=0.0.0.0

# Spring MVC Configuration
spring.mvc.view.prefix=classpath:/templates/
spring.mvc.view.suffix=.html

# Database Configuration - H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# Spring Data Configuration
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.one-indexed-parameters=true

# Logging Configuration
logging.level.org.springframework=INFO
logging.level.com.booksphere=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin
spring.security.user.roles=ADMIN

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.servlet.content-type=text/html

# Multipart File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Disable Spring Boot Favicon
spring.mvc.favicon.enabled=false

# Date Format Configuration
spring.mvc.format.date=yyyy-MM-dd
spring.mvc.format.date-time=yyyy-MM-dd HH:mm:ss
spring.mvc.format.time=HH:mm:ss

# Disable schema.sql and data.sql execution while using Hibernate ddl-auto
spring.sql.init.mode=never
spring.jpa.defer-datasource-initialization=false

# Allow circular references
spring.main.allow-circular-references=true
"@ | Out-File -FilePath $propertiesPath -Encoding UTF8
}

Write-Host "Project setup completed!"
Write-Host "You can now run the project using:"
Write-Host "./mvnw spring-boot:run" 