FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到容器中
COPY target/home-service-0.0.1-SNAPSHOT.jar /app/home-service.jar

# 暴露服务端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "home-service.jar"]