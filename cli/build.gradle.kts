import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsPublish)
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.dependencyAnalysis)
  application
}

dependencies {
  implementation(projects.lib)

  testImplementation(kotlin("test"))
}

application {
  mainClass = "com.eygraber.vice.module.generator.cli.ModuleGeneratorCliKt"
}

tasks.jar {
  manifest {
    attributes["Main-Class"] = "com.eygraber.vice.module.generator.cli.ModuleGeneratorCliKt"
  }
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  from(configurations.runtimeClasspath.get().map { if(it.isDirectory) it else zipTree(it) })
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
