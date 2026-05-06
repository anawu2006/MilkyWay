plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.milkyway"
version = "1.2.0"

gradlePlugin {
    plugins {
        create("rotatingComposable") {
            id = "com.milkyway.rotatingComposable"
            implementationClass = "com.milkyway.RotatingComposablePlugin"
        }
    }
}
