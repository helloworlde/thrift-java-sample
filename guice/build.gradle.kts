plugins {
    java
    idea
    application
    id("io.freefair.lombok") version "5.3.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

val thriftVersion = "0.23.1"
val niftyVersion = "0.23.0"
val slf4jVersion = "1.7.25"

dependencies {
    implementation("com.facebook.swift:swift-root:${thriftVersion}")
    implementation("com.facebook.swift:swift-service:${thriftVersion}")
    implementation("com.facebook.swift:swift-annotations:${thriftVersion}")
    implementation("com.facebook.nifty:nifty-client:${niftyVersion}")
    implementation("com.facebook.nifty:nifty-core:${niftyVersion}")

    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("org.slf4j:slf4j-simple:${slf4jVersion}")

    testImplementation("junit:junit:4.13")
}

