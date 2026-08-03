plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
}

android {
  namespace = "com.example.screens.test"
}

dependencies {
  api(libs.androidx.navigation3.runtime)
  api(libs.kotlinx.serialization.core)
}
