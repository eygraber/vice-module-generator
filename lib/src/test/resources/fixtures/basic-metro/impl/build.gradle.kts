plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsCompose)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.metro)
  alias(libs.plugins.paparazzi)
}

@Suppress("DEPRECATION")
android {
  namespace = "com.example.screens.test.impl"
}

dependencies {
  api(projects.di)
  api(projects.nav.public)
  api(projects.screens.testFeature.public)
  api(libs.androidx.navigation3.runtime)
  api(libs.compose.runtime)
  api(libs.vice.core)
  api(libs.vice.nav3)

  implementation(projects.ui.compose)
  implementation(projects.ui.material)
  implementation(libs.compose.foundation)
  implementation(libs.compose.foundationLayout)
  implementation(libs.compose.material3)
  implementation(libs.compose.runtimeAnnotation)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.text)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.kotlinx.coroutines.core)

  debugImplementation(libs.compose.ui.tooling)

  testImplementation(projects.testUtils)
  testImplementation(libs.bundles.test.paparazzi)
}
