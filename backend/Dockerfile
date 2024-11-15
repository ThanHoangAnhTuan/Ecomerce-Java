# Sử dụng một image Maven để build ứng dụng
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy các file pom.xml và source code vào container
COPY backend/pom.xml ./pom.xml
COPY backend/src ./src

# Chạy Maven để build ứng dụng và tạo file .jar
RUN mvn clean package -DskipTests -f ./pom.xml

# Sử dụng một image Java để chạy ứng dụng
FROM openjdk:21

# Thiết lập thư mục làm việc
WORKDIR /app/backend

# Copy file .jar từ image build vào container
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
