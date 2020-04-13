FROM openjdk:12
WORKDIR /usr/src/Project
RUN yum -y install libX11-devel.x86_64
RUN yum -y install libXext.x86_64
RUN yum -y install libXrender.x86_64
RUN yum -y install libXtst.x86_64
COPY CS1660Project-0.0.1-SNAPSHOT-shaded.jar /usr/src/Project
CMD ["java", "-jar", "CS1660Project-0.0.1-SNAPSHOT-shaded.jar"]