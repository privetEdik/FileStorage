# 1. Используем официальный образ Maven для сборки приложения
FROM maven:3.8.5-openjdk-17 AS build

# 2. Устанавливаем рабочую директорию
WORKDIR /app

# 3. Копируем файл pom.xml и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 4. Копируем весь проект и собираем его
COPY src ./src
RUN mvn clean package -DskipTests

# 5. Используем минимальный образ с JDK 17 для запуска приложения
FROM openjdk:17-jdk-slim

# 6. Устанавливаем рабочую директорию для runtime
WORKDIR /app

# 7. Копируем сгенерированный jar файл из этапа сборки
COPY --from=build /app/target/FileStorage-0.0.1.jar /app/FileStorage-0.0.1.jar

# 8. Добавляем переменные окружения, если необходимо
#ENV JAVA_OPTS=""

# 9. Указываем команду запуска приложения
ENTRYPOINT ["java", "-jar", "/app/FileStorage-0.0.1.jar"]

# 10. Expose порта, который используется в приложении
EXPOSE 8080