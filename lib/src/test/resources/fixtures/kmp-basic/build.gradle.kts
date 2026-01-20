import org.gradle.kotlin.dsl.dependencies

plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.paparazzi)
}

val pkg = "com.example.screens.test"

compose {
  resources {
    packageOfResClass = pkg
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = pkg,
  )

  androidLibrary {
    androidResources.enable = true

    withHostTest {
      isIncludeAndroidResources = true
    }
  }

  kspDependenciesForAllTargets {
    ksp(libs.kotlinInject.anvilCompiler)
  }

  sourceSets {
    // https://youtrack.jetbrains.com/issue/KT-83321/
    named("androidHostTest").dependencies {
      implementation(projects.testUtils)
      implementation(libs.bundles.test.paparazzi)
    }

    commonMain.dependencies {
      api(projects.di)

      implementation(projects.ui.compose)
      implementation(projects.ui.material)

      implementation(libs.compose.foundation)
      implementation(libs.compose.foundationLayout)
      implementation(libs.compose.material3)
      implementation(libs.compose.nav3.runtime)
      implementation(libs.compose.resources)
      implementation(libs.compose.runtime)
      implementation(libs.compose.runtimeAnnotation)
      implementation(libs.compose.ui)
      implementation(libs.compose.ui.text)
      implementation(libs.compose.uiToolingPreview)

      implementation(libs.kotlinInject.anvilRuntime)
      implementation(libs.kotlinInject.anvilRuntimeOptional)
      implementation(libs.kotlinInject.runtime)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.core)

      implementation(libs.vice.core)
      implementation(libs.vice.nav3)
    }
  }
}

dependencies {
  androidRuntimeClasspath(libs.compose.uiToolingPreviewIde)
}
