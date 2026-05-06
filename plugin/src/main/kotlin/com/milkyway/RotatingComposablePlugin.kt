package com.milkyway

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.net.URI

class RotatingComposablePlugin : Plugin<Project> {

    private val groupId = "com.milkyway"
    private val artifactId = "imaging-fused"
    private val version = "1.2.0"
    private val imagingFusedAarUrl = "https://github.com/anawu2006/MilkyWay/raw/refs/heads/main/libs/imaging-fused.aar"

    private val aarDependencies = listOf(
        "com.getkeepsafe.relinker:relinker:1.4.5",
        "org.apache.commons:commons-compress:1.28.0",
        "org.jetbrains.kotlinx:kotlinx-serialization-core:1.10.0",
        "com.charleskorn.kaml:kaml:0.104.0",
        "com.jakewharton.timber:timber:5.0.1",
        "io.arrow-kt:arrow-core-data:0.12.1",
    )

    override fun apply(project: Project) {
        val extension = project.extensions.create("milkyway", MilkyWayExtension::class.java)

        project.afterEvaluate {
            val accessKey = extension.s3AccessKey
            val secret = extension.s3Secret
            if (accessKey.isNullOrBlank() || secret.isNullOrBlank()) {
                throw GradleException(
                    "Set s3AccessKey and s3Secret in the milkyway { ... } block to download imaging-fused.aar."
                )
            }

            publishToMavenLocal(accessKey, secret)
            project.dependencies.add("implementation", "$groupId:$artifactId:$version")
            aarDependencies.forEach { project.dependencies.add("implementation", it) }
        }
    }

    // accessKey/secret will sign a SigV4 S3 request once that path is wired up;
    // for now they're only validated for presence and the static presigned URL is used.
    @Suppress("UNUSED_PARAMETER")
    private fun publishToMavenLocal(accessKey: String, secret: String) {
        val mavenLocal = java.io.File(System.getProperty("user.home"), ".m2/repository")
        val artifactDir = mavenLocal
            .resolve(groupId.replace('.', '/'))
            .resolve(artifactId)
            .resolve(version)

        val aarFile = artifactDir.resolve("$artifactId-$version.aar")
        if (aarFile.exists()) return

        artifactDir.mkdirs()

        println("Downloading $artifactId-$version.aar...")
        URI(imagingFusedAarUrl).toURL().openStream().use { input ->
            aarFile.outputStream().use { output -> input.copyTo(output) }
        }
        println("Published $artifactId-$version.aar to local Maven.")

        artifactDir.resolve("$artifactId-$version.pom").writeText(
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>$groupId</groupId>
              <artifactId>$artifactId</artifactId>
              <version>$version</version>
              <packaging>aar</packaging>
            </project>
            """.trimIndent()
        )
    }
}
