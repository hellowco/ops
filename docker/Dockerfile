# Base image 복사
FROM openjdk:17-jdk-alpine

# root-ca 복사
#WORKDIR /var/jenkins_home/custom/llmops-api
COPY root-ca.der /root-ca.der

# 키 생성
RUN keytool -import -trustcacerts -alias opensearch \
    -file /tmp/root-ca.der \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit -noprompt

# JAR 복사
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
