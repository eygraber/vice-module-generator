import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsPublish)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinJvm)
}

dependencies {
  implementation(projects.lib)

  implementation(compose.desktop.currentOs)

  implementation(libs.compose.foundation)
  implementation(libs.compose.foundationLayout)
  implementation(libs.compose.material3)
  implementation(libs.compose.runtime)
  implementation(libs.compose.runtimeAnnotations)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.text)
  implementation(libs.compose.ui.unit)
  implementation(libs.compose.ui.util)

  implementation(libs.kotlinx.coroutines.core)
  runtimeOnly(libs.kotlinx.coroutines.swing)
}

tasks.jar {
  manifest {
    attributes["Main-Class"] = "com.eygraber.vice.module.generator.ui.ModuleGeneratorKt"
  }
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  from(configurations.runtimeClasspath.get().map { if(it.isDirectory) it else zipTree(it) })
}

compose.desktop {
  application {
    mainClass = "com.eygraber.vice.module.generator.ui.ModuleGeneratorKt"
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}

dependencyAnalysis {
  issues {
    onUnusedDependencies {
      excludeRegex("org.jetbrains.compose.desktop:.*")
      exclude("org.jetbrains.compose.hot-reload:hot-reload-runtime-api")
    }

    onIncorrectConfiguration {
      severity("ignore")
    }
  }
}
