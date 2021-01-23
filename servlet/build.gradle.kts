plugins {
    java
    idea
    application
    war
    id("io.freefair.lombok") version "5.3.0"
    id("org.jruyi.thrift") version "0.4.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

val thriftVersion = "0.13.0-SNAPSHOT"
val slf4jVersion = "1.7.25"
val tomcatVersion = "9.0.26"
val servletVersion = "3.0.1"

dependencies {
    implementation("org.apache.thrift:libthrift:${thriftVersion}")
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("org.slf4j:slf4j-simple:${slf4jVersion}")

    implementation("org.apache.tomcat.embed:tomcat-embed-core:${tomcatVersion}")
    implementation("org.apache.tomcat.embed:tomcat-embed-jasper:${tomcatVersion}")
    implementation("javax.servlet:javax.servlet-api:${servletVersion}")

    testImplementation("junit:junit:4.13")
}
