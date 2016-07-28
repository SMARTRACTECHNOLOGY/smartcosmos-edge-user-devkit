FROM smartcosmos/service
MAINTAINER SMART COSMOS Platform Core Team

ADD target/smartcosmos-*.jar  /opt/smartcosmos/smartcosmos-edge-user-devkit.jar

CMD ["/opt/smartcosmos/smartcosmos-edge-user-devkit.jar"]
