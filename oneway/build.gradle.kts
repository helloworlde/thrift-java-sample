plugins {
    java
    idea
    application
    id("io.freefair.lombok") version "5.3.0"
    id("org.jruyi.thrift") version "0.4.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

val thriftVersion = "0.13.0"
val slf4jVersion = "1.7.25"

dependencies {
    implementation("org.apache.thrift:libthrift:${thriftVersion}")
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("org.slf4j:slf4j-simple:${slf4jVersion}")

    testImplementation("junit:junit:4.13")
}

