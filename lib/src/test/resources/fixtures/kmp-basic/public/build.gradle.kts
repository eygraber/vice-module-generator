plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.example.screens.test",
  )

  sourceSets {
    commonMain.dependencies {
      api(libs.compose.nav3.runtime)
      api(libs.kotlinx.serialization.core)
    }
  }
}
