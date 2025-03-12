# Используем официальный образ Tomcat 9
FROM tomcat:9.0

# Копируем собранный WAR-файл в директорию Tomcat (webapps)
COPY target/your-war-name.war /usr/local/tomcat/webapps/

# Открываем порт 8080 для доступа к Tomcat
EXPOSE 8080

# Запускаем Tomcat
CMD ["catalina.sh", "run"]