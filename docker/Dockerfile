# Base image 설정
FROM openjdk:17-jdk-alpine

# 인증서 파일 복사
COPY root-ca.der /tmp/root-ca.der

# 인증서 추가
RUN keytool -import -trustcacerts -alias opensearch \
    -file /tmp/root-ca.der \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit -noprompt

# JAR 파일을 컨테이너에 복사
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
