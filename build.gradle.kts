plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "io.hasura"
version = "2.0.1"


repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://raw.githubusercontent.com/hasura/jvm-ndc-ir/m2repo")
    }
}

dependencies {
    api("io.hasura:ndc-ir:2.0.1")
    api("org.jooq:jooq:3.18.3")

    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    implementation("com.google.guava:guava:33.0.0-jre") {
        because("Used in QueryRequestRelationGraph to create a graph of table relations")
    }

    api(kotlin("script-runtime"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
}

java {
    withSourcesJar()
}

publishing {

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/hasura/jvm-ndc-sqlgen")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GH_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GH_TOKEN")
            }
        }
    }
}

