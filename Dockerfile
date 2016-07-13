
FROM smartcosmos/java
MAINTAINER SMART COSMOS Platform Core Team

ENV CONFIG_FILE_URI 'https://github.com/SMARTRACTECHNOLOGY/smartcosmos-cluster-config'
ENV CONFIG_SERVER_LABEL 'master'
ENV ENCRYPT_KEY ''

# if you want to use a github token set the USERNAME to be the token
# the password needs to be empty when using a token
ENV SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME ''
ENV SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD ''

ADD target/smartcosmos-*.jar  /opt/smartcosmos/smartcosmos-config-server.jar


EXPOSE 45371

CMD ["java", "-jar", "/opt/smartcosmos/smartcosmos-edge-user-devkit.jar"]
